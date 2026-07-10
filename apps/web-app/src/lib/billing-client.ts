// Client gọi sang apps/billing-service (Spring Boot, deploy Cloud Run)
// khi cần trigger job tính toán/sinh hoá đơn thay vì query thẳng Postgres.

const BILLING_SERVICE_URL = process.env.BILLING_SERVICE_URL ?? "http://localhost:8082";

export async function generateInvoice(contractId: number, accessToken: string) {
  const res = await fetch(`${BILLING_SERVICE_URL}/api/invoices/generate`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${accessToken}`,
    },
    body: JSON.stringify({ contractId }),
  });

  if (!res.ok) {
    throw new Error(`billing-service error: ${res.status}`);
  }
  return res.json();
}
