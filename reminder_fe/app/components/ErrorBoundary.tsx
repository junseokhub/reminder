"use client";

import { Component, ReactNode } from "react";

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export default class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          height: "100vh",
          gap: "16px",
          fontFamily: "-apple-system, BlinkMacSystemFont, sans-serif",
        }}>
          <h2 style={{ color: "#1c1c1e", fontSize: "20px" }}>문제가 발생했습니다</h2>
          <p style={{ color: "#8e8e93", fontSize: "14px" }}>
            {this.state.error?.message || "알 수 없는 오류"}
          </p>
          <button
            onClick={() => {
              this.setState({ hasError: false, error: null });
              window.location.reload();
            }}
            style={{
              padding: "8px 20px",
              borderRadius: "8px",
              border: "none",
              background: "#007AFF",
              color: "white",
              fontSize: "14px",
              cursor: "pointer",
            }}
          >
            새로고침
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
