import clsx from "clsx";

const palette: Record<string, string> = {
  // Booking / ChangeRequest statuses
  PENDING:   "bg-yellow-100 text-yellow-800",
  CONFIRMED: "bg-green-100 text-green-800",
  CANCELLED: "bg-red-100 text-red-800",
  COMPLETED: "bg-blue-100 text-blue-800",
  APPROVED:  "bg-green-100 text-green-800",
  REJECTED:  "bg-red-100 text-red-800",
  REVOKED:   "bg-gray-100 text-gray-700",
  OFFERED:   "bg-purple-100 text-purple-800",
  // IATA event codes
  RCS: "bg-sky-100 text-sky-800",
  MAN: "bg-indigo-100 text-indigo-800",
  PRE: "bg-violet-100 text-violet-800",
  DEP: "bg-orange-100 text-orange-800",
  ARR: "bg-teal-100 text-teal-800",
  RCF: "bg-cyan-100 text-cyan-800",
  NFD: "bg-blue-100 text-blue-800",
  DLV: "bg-green-100 text-green-800",
  CCD: "bg-emerald-100 text-emerald-800",
  TFD: "bg-pink-100 text-pink-800",
  // Segment statuses
  PLANNED:   "bg-gray-100 text-gray-700",
  DEPARTED:  "bg-orange-100 text-orange-800",
  ARRIVED:   "bg-teal-100 text-teal-800",
  CREATED:   "bg-sky-100 text-sky-800",
  UPDATED:   "bg-blue-100 text-blue-800",
  DELETED:   "bg-red-100 text-red-800",
};

export default function StatusBadge({ status }: { status: string }) {
  return (
    <span className={clsx(
      "inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold",
      palette[status?.toUpperCase()] ?? "bg-gray-100 text-gray-700"
    )}>
      {status}
    </span>
  );
}