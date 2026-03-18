"use client";

import { useState, useEffect, useRef } from "react";
import styles from "./ListModal.module.css";

const PRESET_COLORS = [
  "#FF3B30", "#FF9500", "#FFCC00", "#34C759",
  "#00C7BE", "#007AFF", "#5856D6", "#AF52DE",
  "#FF2D55", "#A2845E", "#8E8E93", "#1C3A5F",
];

interface ListModalProps {
  isOpen: boolean;
  initialName?: string;
  initialColor?: string;
  onClose: () => void;
  onSave: (name: string, color: string) => void;
}

export default function ListModal({
  isOpen,
  initialName = "",
  initialColor = "#007AFF",
  onClose,
  onSave,
}: ListModalProps) {
  const [name, setName] = useState(initialName);
  const [color, setColor] = useState(initialColor);
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setName(initialName);
    setColor(initialColor);
  }, [initialName, initialColor, isOpen]);

  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") onClose();
      if (e.key === "Tab" && modalRef.current) {
        const focusable = modalRef.current.querySelectorAll<HTMLElement>(
          "input, button, [tabindex]"
        );
        if (focusable.length === 0) return;
        const first = focusable[0];
        const last = focusable[focusable.length - 1];
        if (e.shiftKey && document.activeElement === first) {
          e.preventDefault();
          last.focus();
        } else if (!e.shiftKey && document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    };
    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const handleSave = () => {
    if (!name.trim()) return;
    onSave(name.trim(), color);
  };

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div ref={modalRef} className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <h3 className={styles.title}>
          {initialName ? "목록 수정" : "새로운 목록"}
        </h3>
        <input
          className={styles.nameInput}
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="목록 이름"
          autoFocus
          onKeyDown={(e) => e.key === "Enter" && handleSave()}
        />
        <div className={styles.colorGrid}>
          {PRESET_COLORS.map((c) => (
            <button
              key={c}
              className={`${styles.colorCircle} ${
                color === c ? styles.selected : ""
              }`}
              style={{ backgroundColor: c }}
              onClick={() => setColor(c)}
            />
          ))}
        </div>
        <div className={styles.actions}>
          <button className={styles.cancelButton} onClick={onClose}>
            취소
          </button>
          <button
            className={styles.saveButton}
            onClick={handleSave}
            disabled={!name.trim()}
          >
            {initialName ? "수정" : "생성"}
          </button>
        </div>
      </div>
    </div>
  );
}
