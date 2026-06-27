import { AdminOrderManagementApi, type OrderItemDTO } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { DataTable } from '@/components/table/Table'
import { Badge } from '@/components/ui/badge'
import { formatDate } from '@/lib/utils'
import { orderItemStatusTranslations } from '@/data/enum-translations';

export const Route = createFileRoute('/admin/order/history')({
  component: RouteComponent,
})

function RouteComponent() {
  const {getApiClient} = useAuth();
  const orderApi = getApiClient(AdminOrderManagementApi);

  const {data: orderItems, isLoading: orderHistoryLoading} = useQuery({
    queryKey: ['orderHistory'],
    queryFn: async () => (await orderApi.getAllOrdersItems()).data,
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  });

  // Status options for filtering
  const statusOptions = Object.entries(orderItemStatusTranslations).map(([value, label]) => ({
    label,
    value
  }));

  const columns: ColumnDef<OrderItemDTO>[] = [
    {
      accessorKey: 'id',
      header: 'Reference',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => <div>{row.getValue('id')}</div>,
    },
    {
      accessorKey: 'name',
      header: 'Item',
      meta: {
        filterVariant: 'string'
      },
    },
    {
      accessorKey: 'quantity',
      header: 'Quantity',
      meta: {
        filterVariant: 'numeric-range'
      },
    },
    {
      accessorKey: 'deliveryDate',
      header: 'Delivery Date',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: 'date-range'
      },
      cell: ({ row }) => formatDate(row.getValue('deliveryDate')),
    },
    {
      accessorKey: 'status',
      header: 'Status',
      filterFn: 'multiSelect' as any,
      meta: {
        filterVariant: 'multi-select',
        filterOptions: statusOptions
      },
      cell: ({ row }) => {
        const status = row.getValue('status') as string;
        const statusText = orderItemStatusTranslations[status] || status;
        
        let variant: 'default' | 'secondary' | 'destructive' | 'outline' = 'default';
        
        if (status === 'DELIVERED') {
          variant = 'default';
        } else if (status === 'IN_PROGRESS' || status === 'IN_DELIVERY') {
          variant = 'secondary';
        } else if (status === 'CANCELED' || status === 'REFUNDED') {
          variant = 'destructive';
        } else {
          variant = 'outline';
        }
        
        return (
          <Badge variant={variant}>
            {statusText}
          </Badge>
        );
      },
    },
    {
      accessorKey: 'totalPrice',
      header: 'Price',
      filterFn: 'numericRange' as any,
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => `R${(row.getValue('totalPrice') as number)?.toFixed(2) || '0.00'}`,
    },
  ];

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Order History</h1>
        <p className="text-muted-foreground">
          View all orders and their status
        </p>
      </div>

      <DataTable
        columns={columns}
        data={orderItems || []}
        enableSorting
        enableFiltering
        enableSearching
        searchPlaceholder="Search orders..."
        pageSize={10}
        loading={orderHistoryLoading}
        emptyMessage="No order history found."
        className="space-y-4"
      />
    </div>
  );
}
