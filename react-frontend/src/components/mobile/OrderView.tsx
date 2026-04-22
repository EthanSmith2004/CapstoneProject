import { useState } from "react";
import type { OrderItemDTO } from "@/api";
import { formatDateLong } from "@/lib/utils";
import { Ban, ChevronDown, ChevronRight, LoaderIcon, MessageSquareMore, Star } from "lucide-react";
import { FeedbackDialog } from "@/components/bestelling/FeedbackDialog";
import { orderItemStatusTranslations } from "@/data/enum-translations";

interface OrderViewProps {
  orders?: OrderItemDTO[];
  loading: boolean;
  completed?: boolean;
  title?: string;
  defaultOpen?: boolean;
  cancelOrder?: (orderId: number) => void;
}

export function OrderView({
  orders,
  loading,
  completed = false,
  title,
  defaultOpen = true,
  cancelOrder,
}: OrderViewProps) {
  const [feedbackDialogOpen, setFeedbackDialogOpen] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<OrderItemDTO | null>(null);
  const [isCollapsed, setIsCollapsed] = useState(!defaultOpen);

  const toggleCollapse = () => setIsCollapsed(prev => !prev);

  const groupByDate = (orders: OrderItemDTO[]) =>
    orders.reduce((groups, order) => {
      const date = formatDateLong(order.deliveryDate);
      if (!groups[date]) groups[date] = [];
      groups[date].push(order);
      return groups;
    }, {} as Record<string, OrderItemDTO[]>);

  const handleOpenFeedback = (order: OrderItemDTO) => {
    setSelectedOrder(order);
    setFeedbackDialogOpen(true);
  };

  const renderStarRating = (rating?: number) => {
    if (!rating) return <span className="text-muted-foreground">Geen gradering</span>;

    return (
      <div className="flex items-center gap-1">
        {[...Array(5)].map((_, index) => (
          <Star
            key={index}
            className={`h-4 w-4 ${index < rating ? "fill-yellow-400 text-yellow-400" : "text-gray-300"}`}
          />
        ))}
        <span className="ml-1 text-sm font-medium">{rating}/5</span>
      </div>
    );
  };

  return (
    <div className="border-t">
     
      <div
  className="flex justify-between items-center px-4 py-3 cursor-pointer rounded-lg shadow-sm border bg-white w-full"
  onClick={toggleCollapse}
>


        <h2 className="font-bold text-lg">{title}</h2>
        {isCollapsed ? <ChevronRight className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
      </div>

      {!isCollapsed && (
        <>
          {loading && (
            <div className="flex justify-center w-full py-4">
              <LoaderIcon className="animate-spin" />
            </div>
          )}

          {!loading && (!orders || orders.length === 0) && (
            <p className="p-4 text-center text-gray-500">
              Geen {completed ? "verlede" : "huidige"} bestellings nie.
            </p>
          )}

          {!loading && orders && orders.length > 0 && (
            <div className="flex flex-col gap-4">
              {Object.entries(groupByDate(orders)).map(([date, ordersOnDate]) => (
                <div key={date} className="border-t pt-2">
                  <h3 className="px-4 py-2 font-semibold bg-gray-100">
                    Vir aflewering op {date}
                  </h3>
                  {ordersOnDate.map((order) => {
                    const canEdit =
                      order.status === "PAID" &&
                      !completed &&
                      order.editBy &&
                      new Date(order.editBy) > new Date();

                    return (
                      <div key={order.id} className="p-4 border-b last:border-0">
                        <div className="flex justify-between">
                          <div>
                            <p className="font-medium">Item #{order.id}: {order.name}</p>
                            <p className="text-sm text-gray-600">
                              {order.quantity} @ R{order.price}
                            </p>
                            <p className="text-sm text-gray-600">
                              Status: {orderItemStatusTranslations[order.status ?? "UNK"]}
                            </p>
                            {canEdit ? (
                              <p className="text-sm text-blue-600">
                                Jy kan hierdie bestelling kanselleer voor {formatDateLong(order.editBy!)}
                              </p>
                            ) : (
                              <p className="text-sm text-gray-600">
                                Hierdie bestelling kan nie meer gekanselleer word nie.
                              </p>
                            )}
                            {order.feedback && (
                              <div className="text-sm mt-2">
                                {renderStarRating(order.feedback.rating)}
                                <div className="mt-1">{order.feedback.comment}</div>
                              </div>
                            )}
                          </div>

                          <div className="text-right">
                            <p className="font-medium">
                              Totaal: R{(order.totalPrice ?? 0).toFixed(2)}
                            </p>
                            {canEdit && (
                              <Ban
                                className="inline-block ml-2 cursor-pointer hover:text-red-600"
                                onClick={() => cancelOrder && order.id && cancelOrder(order.id)}
                              />
                            )}
                            {completed && !order.feedback && (
                              <MessageSquareMore
                                className="inline-block ml-2 cursor-pointer hover:text-blue-600"
                                onClick={() => handleOpenFeedback(order)}
                              />
                            )}
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              ))}
            </div>
          )}

          {selectedOrder && selectedOrder.id && (
            <FeedbackDialog
              open={feedbackDialogOpen}
              onOpenChange={setFeedbackDialogOpen}
              menuItemId={selectedOrder.id}
              itemName={selectedOrder.name}
            />
          )}
        </>
      )}
    </div>
  );
}
