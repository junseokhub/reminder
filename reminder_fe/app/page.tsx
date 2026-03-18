"use client";

import { useEffect, useState, useCallback, useRef } from "react";
import Sidebar from "./components/Sidebar";
import ReminderListView from "./components/ReminderListView";
import ListModal from "./components/ListModal";
import ConfirmModal from "./components/ConfirmModal";
import { listApi, reminderApi } from "./lib/api";
import { Reminder, ReminderList } from "./lib/types";
import styles from "./page.module.css";

export default function Home() {
  const [lists, setLists] = useState<ReminderList[]>([]);
  const [selectedListId, setSelectedListIdState] = useState<number | null>(() => {
    if (typeof window !== "undefined") {
      const params = new URLSearchParams(window.location.search);
      const id = params.get("list");
      return id ? Number(id) : null;
    }
    return null;
  });
  const [reminders, setReminders] = useState<Reminder[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingList, setEditingList] = useState<ReminderList | null>(null);
  const [deleteListTarget, setDeleteListTarget] = useState<ReminderList | null>(null);
  const setSelectedListId = useCallback((id: number | null) => {
    setSelectedListIdState(id);
    const url = new URL(window.location.href);
    if (id !== null) {
      url.searchParams.set("list", String(id));
    } else {
      url.searchParams.delete("list");
    }
    window.history.replaceState({}, "", url.toString());
  }, []);

  const selectedListIdRef = useRef(selectedListId);
  selectedListIdRef.current = selectedListId;

  const loadLists = useCallback(async () => {
    const data = await listApi.findAll();
    setLists(data);
    if (data.length > 0 && selectedListIdRef.current === null) {
      setSelectedListId(data[0].id);
    }
  }, []);

  const loadReminders = useCallback(async () => {
    if (selectedListId === null) return;
    const data = await reminderApi.findByListId(selectedListId);
    setReminders(data);
  }, [selectedListId]);

  useEffect(() => {
    loadLists();
  }, []);

  useEffect(() => {
    loadReminders();
  }, [selectedListId]);

  const handleDataChange = async () => {
    await loadReminders();
    await loadLists();
  };

  const handleAddList = () => {
    setEditingList(null);
    setModalOpen(true);
  };

  const handleEditList = (list: ReminderList) => {
    setEditingList(list);
    setModalOpen(true);
  };

  const handleDeleteList = (list: ReminderList) => {
    setDeleteListTarget(list);
  };

  const confirmDeleteList = async () => {
    if (!deleteListTarget) return;
    await listApi.delete(deleteListTarget.id);
    if (selectedListId === deleteListTarget.id) {
      setSelectedListId(lists.find((l) => l.id !== deleteListTarget.id)?.id ?? null);
    }
    setDeleteListTarget(null);
    await loadLists();
  };

  const handleModalSave = async (name: string, color: string) => {
    if (editingList) {
      await listApi.update(editingList.id, name, color);
    } else {
      const created = await listApi.create(name, color);
      setSelectedListId(created.id);
    }
    setModalOpen(false);
    await loadLists();
  };

  const selectedList = lists.find((l) => l.id === selectedListId);

  return (
    <div className={styles.main}>
      <Sidebar
        lists={lists}
        selectedListId={selectedListId}
        onSelectList={setSelectedListId}
        onAddList={handleAddList}
        onEditList={handleEditList}
        onDeleteList={handleDeleteList}
      />
      {selectedList ? (
        <ReminderListView
          list={selectedList}
          reminders={reminders}
          onDataChange={handleDataChange}
        />
      ) : (
        <div className={styles.emptyState}>목록을 선택하세요</div>
      )}
      <ListModal
        isOpen={modalOpen}
        initialName={editingList?.name}
        initialColor={editingList?.color}
        onClose={() => setModalOpen(false)}
        onSave={handleModalSave}
      />
      <ConfirmModal
        isOpen={deleteListTarget !== null}
        message={`"${deleteListTarget?.name}" 목록을 삭제하시겠습니까?`}
        onConfirm={confirmDeleteList}
        onCancel={() => setDeleteListTarget(null)}
      />
    </div>
  );
}
