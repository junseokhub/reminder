import type { Metadata } from "next";
import ErrorBoundary from "./components/ErrorBoundary";
import "./globals.css";

export const metadata: Metadata = {
  title: "TobyReminder",
  description: "Apple Reminders Web Clone",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body><ErrorBoundary>{children}</ErrorBoundary></body>
    </html>
  );
}
