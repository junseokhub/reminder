"use client";

import { useState, useRef, useEffect } from "react";
import { ReminderList } from "../lib/types";
import styles from "./Sidebar.module.css";

interface SidebarProps {
  lists: ReminderList[];
  selectedListId: number | null;
  onSelectList: (id: number) => void;
  onAddList: () => void;
  onEditList: (list: ReminderList) => void;
  onDeleteList: (list: ReminderList) => void;
}

export default function Sidebar({
  lists,
  selectedListId,
  onSelectList,
  onAddList,
  onEditList,
  onDeleteList,
}: SidebarProps) {
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    list: ReminderList;
  } | null>(null);
  const contextRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClick = () => setContextMenu(null);
    document.addEventListener("click", handleClick);
    return () => document.removeEventListener("click", handleClick);
  }, []);

  useEffect(() => {
    if (!contextMenu) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        setContextMenu(null);
      } else if (e.key === "ArrowDown" || e.key === "ArrowUp") {
        e.preventDefault();
        const buttons = contextRef.current?.querySelectorAll<HTMLButtonElement>("button");
        if (!buttons || buttons.length === 0) return;
        const current = Array.from(buttons).indexOf(document.activeElement as HTMLButtonElement);
        const next = e.key === "ArrowDown"
          ? (current + 1) % buttons.length
          : (current - 1 + buttons.length) % buttons.length;
        buttons[next].focus();
      }
    };
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [contextMenu]);

  useEffect(() => {
    if (contextMenu && contextRef.current) {
      const rect = contextRef.current.getBoundingClientRect();
      const adjustedX = Math.min(contextMenu.x, window.innerWidth - rect.width - 8);
      const adjustedY = Math.min(contextMenu.y, window.innerHeight - rect.height - 8);
      if (adjustedX !== contextMenu.x || adjustedY !== contextMenu.y) {
        setContextMenu({ ...contextMenu, x: adjustedX, y: adjustedY });
      }
      const firstButton = contextRef.current.querySelector<HTMLButtonElement>("button");
      firstButton?.focus();
    }
  }, [contextMenu]);

  const handleContextMenu = (e: React.MouseEvent, list: ReminderList) => {
    e.preventDefault();
    setContextMenu({ x: e.clientX, y: e.clientY, list });
  };

  return (
    <aside className={styles.sidebar}>
      <div className={styles.header}>
        <h2 className={styles.sectionTitle}>나의 목록</h2>
      </div>
      <ul className={styles.listItems}>
        {lists.map((list) => (
          <li
            key={list.id}
            className={`${styles.listItem} ${
              selectedListId === list.id ? styles.selected : ""
            }`}
            onClick={() => onSelectList(list.id)}
            onContextMenu={(e) => handleContextMenu(e, list)}
          >
            <span
              className={styles.bullet}
              style={{ backgroundColor: list.color }}
            />
            <span className={styles.listName}>{list.name}</span>
            <span className={styles.count}>
              {list.reminderCount || 0}
            </span>
          </li>
        ))}
      </ul>
      <button className={styles.addButton} onClick={onAddList}>
        + 목록 추가
      </button>

      {contextMenu && (
        <div
          ref={contextRef}
          className={styles.contextMenu}
          style={{ top: contextMenu.y, left: contextMenu.x }}
          onClick={(e) => e.stopPropagation()}
        >
          <button
            className={styles.contextMenuItem}
            onClick={() => {
              onEditList(contextMenu.list);
              setContextMenu(null);
            }}
          >
            수정
          </button>
          {!contextMenu.list.isDefault && (
            <button
              className={`${styles.contextMenuItem} ${styles.deleteItem}`}
              onClick={() => {
                onDeleteList(contextMenu.list);
                setContextMenu(null);
              }}
            >
              삭제
            </button>
          )}
        </div>
      )}
    </aside>
  );
}
