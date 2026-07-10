import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "PropertyOS",
  description: "Hệ thống quản lý bất động sản cho thuê",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="vi">
      <body>{children}</body>
    </html>
  );
}
