import { OrderStatus } from '@/lib/types';
import clsx from 'clsx';

const STATUS_CONFIG: Record<OrderStatus, { label: string; classes: string }> = {
  PENDING:   { label: 'Pending',   classes: 'bg-yellow-100 text-yellow-800' },
  CONFIRMED: { label: 'Confirmed', classes: 'bg-blue-100 text-blue-800' },
  PAID:      { label: 'Paid',      classes: 'bg-green-100 text-green-800' },
  SHIPPED:   { label: 'Shipped',   classes: 'bg-indigo-100 text-indigo-800' },
  DELIVERED: { label: 'Delivered', classes: 'bg-emerald-100 text-emerald-800' },
  CANCELLED: { label: 'Cancelled', classes: 'bg-red-100 text-red-800' },
};

export default function OrderStatusBadge({ status }: { status: OrderStatus }) {
  const config = STATUS_CONFIG[status] ?? { label: status, classes: 'bg-gray-100 text-gray-800' };
  return (
    <span className={clsx('badge font-semibold', config.classes)}>
      {config.label}
    </span>
  );
}
