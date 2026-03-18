"use client";

import { useState, useRef, useEffect, useCallback } from "react";
import { Priority, Reminder, ReminderList } from "../lib/types";
import { reminderApi } from "../lib/api";
import ConfirmModal from "./ConfirmModal";
import styles from "./ReminderListView.module.css";

const PRIORITY_LABELS: Record<Priority, string> = {
  NONE: "없음",
  LOW: "!",
  MEDIUM: "!!",
  HIGH: "!!!",
};

interface ReminderListViewProps {
  list: ReminderList;
  reminders: Reminder[];
  onDataChange: () => void;
}

export default function ReminderListView({
  list,
  reminders,
  onDataChange,
}: ReminderListViewProps) {
  const [isAdding, setIsAdding] = useState(false);
  const [newTitle, setNewTitle] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [editData, setEditData] = useState({
    title: "",
    memo: "",
    dueDate: "",
    dueTime: "",
    priority: "NONE" as Priority,
  });
  const newInputRef = useRef<HTMLInputElement>(null);
  const editTitleRef = useRef<HTMLInputElement>(null);
  const editPanelRef = useRef<HTMLDivElement>(null);
  const editDataRef = useRef(editData);
  editDataRef.current = editData;
  const editingIdRef = useRef(editingId);
  editingIdRef.current = editingId;

  useEffect(() => {
    if (isAdding && newInputRef.current) {
      newInputRef.current.focus();
    }
  }, [isAdding]);

  useEffect(() => {
    if (editingId !== null && editTitleRef.current) {
      editTitleRef.current.focus();
    }
  }, [editingId]);

  const handleEditSaveRef = useRef<() => Promise<void>>(null);

  useEffect(() => {
    if (editingId === null) return;
    const handleClickOutside = (e: MouseEvent) => {
      if (editPanelRef.current && !editPanelRef.current.contains(e.target as Node)) {
        handleEditSaveRef.current?.();
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [editingId]);

  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => setError(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [error]);

  const handleToggle = async (reminder: Reminder) => {
    try {
      await reminderApi.toggle(reminder.id);
      onDataChange();
    } catch {
      setError("토글에 실패했습니다.");
    }
  };

  const handleAdd = async () => {
    if (!newTitle.trim()) {
      setIsAdding(false);
      setNewTitle("");
      return;
    }
    try {
      await reminderApi.create(list.id, newTitle.trim());
      setNewTitle("");
      setIsAdding(false);
      onDataChange();
    } catch {
      setError("리마인더 생성에 실패했습니다.");
    }
  };

  const handleAddKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      (e.currentTarget as HTMLInputElement).blur();
    } else if (e.key === "Escape") {
      setIsAdding(false);
      setNewTitle("");
    }
  };

  const startEdit = (reminder: Reminder) => {
    setEditingId(reminder.id);
    setEditData({
      title: reminder.title,
      memo: reminder.memo || "",
      dueDate: reminder.dueDate || "",
      dueTime: reminder.dueTime ? reminder.dueTime.slice(0, 5) : "",
      priority: reminder.priority,
    });
  };

  const handleEditSave = useCallback(async () => {
    if (editingIdRef.current === null) return;
    const data = editDataRef.current;
    if (data.title.trim()) {
      try {
        await reminderApi.update(editingIdRef.current, {
          title: data.title.trim(),
          memo: data.memo || null,
          dueDate: data.dueDate || null,
          dueTime: data.dueTime ? data.dueTime + ":00" : null,
          priority: data.priority,
        });
      } catch {
        setError("리마인더 수정에 실패했습니다.");
      }
    }
    setEditingId(null);
    onDataChange();
  }, [onDataChange]);

  handleEditSaveRef.current = handleEditSave;

  const handleDelete = async () => {
    if (deleteTarget === null) return;
    try {
      await reminderApi.delete(deleteTarget);
      onDataChange();
    } catch {
      setError("리마인더 삭제에 실패했습니다.");
    }
    setDeleteTarget(null);
  };

  const formatDueDate = (date: string | null) => {
    if (!date) return null;
    const d = new Date(date + "T00:00:00");
    return `${d.getMonth() + 1}월 ${d.getDate()}일`;
  };

  return (
    <div className={styles.container}>
      {error && <div className={styles.toast}>{error}</div>}
      <h1 className={styles.title} style={{ color: list.color }}>
        {list.name}
      </h1>
      <ul className={styles.reminders}>
        {reminders.map((reminder) => (
          <li key={reminder.id} className={styles.reminderItem}>
            <div className={styles.reminderRow} ref={editingId === reminder.id ? editPanelRef : undefined}>
              <button
                className={`${styles.checkbox} ${
                  reminder.completed ? styles.checked : ""
                }`}
                style={{
                  borderColor: list.color,
                  backgroundColor: reminder.completed ? list.color : "transparent",
                }}
                onClick={() => handleToggle(reminder)}
              >
                {reminder.completed && (
                  <svg width="10" height="8" viewBox="0 0 10 8" fill="none" className={styles.checkIcon}>
                    <path d="M1 4L3.5 6.5L9 1" stroke="white" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                )}
              </button>
              <div className={styles.reminderContent} onClick={() => editingId !== reminder.id && startEdit(reminder)}>
                {editingId === reminder.id ? (
                  <div className={styles.editPanel}>
                    <input
                      ref={editTitleRef}
                      className={styles.editInput}
                      value={editData.title}
                      onChange={(e) => setEditData({ ...editData, title: e.target.value })}
                      onKeyDown={(e) => e.key === "Enter" && handleEditSave()}
                    />
                    <textarea
                      className={styles.editMemo}
                      value={editData.memo}
                      placeholder="메모"
                      rows={2}
                      onChange={(e) => setEditData({ ...editData, memo: e.target.value })}
                    />
                    <div className={styles.editFields}>
                      <label className={styles.fieldLabel}>
                        <span>마감일</span>
                        <input
                          type="date"
                          className={styles.fieldInput}
                          value={editData.dueDate}
                          onChange={(e) => setEditData({ ...editData, dueDate: e.target.value })}
                        />
                      </label>
                      <label className={styles.fieldLabel}>
                        <span>마감시간</span>
                        <input
                          type="time"
                          className={styles.fieldInput}
                          value={editData.dueTime}
                          onChange={(e) => setEditData({ ...editData, dueTime: e.target.value })}
                        />
                      </label>
                      <label className={styles.fieldLabel}>
                        <span>우선순위</span>
                        <div className={styles.priorityGroup}>
                          {(["NONE", "LOW", "MEDIUM", "HIGH"] as Priority[]).map((p) => (
                            <button
                              key={p}
                              className={`${styles.priorityButton} ${editData.priority === p ? styles.priorityActive : ""}`}
                              onClick={(e) => { e.stopPropagation(); setEditData({ ...editData, priority: p }); }}
                            >
                              {PRIORITY_LABELS[p]}
                            </button>
                          ))}
                        </div>
                      </label>
                    </div>
                  </div>
                ) : (
                  <>
                    <span className={`${styles.reminderTitle} ${reminder.completed ? styles.completedTitle : ""}`}>
                      {reminder.priority !== "NONE" && (
                        <span className={styles.priorityIcon} style={{ color: list.color }}>
                          {PRIORITY_LABELS[reminder.priority]}{" "}
                        </span>
                      )}
                      {reminder.title}
                    </span>
                    {(reminder.dueDate || reminder.memo) && (
                      <span className={styles.subtext}>
                        {formatDueDate(reminder.dueDate)}
                        {reminder.dueDate && reminder.memo && " · "}
                        {reminder.memo}
                      </span>
                    )}
                  </>
                )}
              </div>
              <button
                className={styles.deleteButton}
                onClick={() => setDeleteTarget(reminder.id)}
              >
                ×
              </button>
            </div>
          </li>
        ))}
        {isAdding && (
          <li className={styles.reminderItem}>
            <div className={styles.reminderRow}>
              <span className={styles.checkbox} style={{ borderColor: list.color }} />
              <input
                ref={newInputRef}
                className={styles.editInput}
                value={newTitle}
                placeholder="새로운 미리 알림"
                onChange={(e) => setNewTitle(e.target.value)}
                onKeyDown={handleAddKeyDown}
                onBlur={handleAdd}
              />
            </div>
          </li>
        )}
      </ul>
      <button
        className={styles.addButton}
        style={{ color: list.color }}
        onClick={() => setIsAdding(true)}
      >
        + 새로운 미리 알림
      </button>
      <ConfirmModal
        isOpen={deleteTarget !== null}
        message="이 리마인더를 삭제하시겠습니까?"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
