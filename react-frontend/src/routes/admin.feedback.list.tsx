import { AdminFeedbackApi } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router'
import { type ColumnDef, type ColumnFiltersState } from '@tanstack/react-table'
import { DataTable } from '@/components/table/Table'
import { formatDate } from '@/lib/utils'
import { Star } from 'lucide-react'
import { useState, useEffect } from 'react';

interface SearchParams {
  startDate?: string;
  endDate?: string;
  minRating?: number;
  maxRating?: number;
  menuItem?: string;
}

export const Route = createFileRoute('/admin/feedback/list')({
  component: RouteComponent,
  validateSearch: (search: Record<string, unknown>): SearchParams => ({
    startDate: search.startDate as string | undefined,
    endDate: search.endDate as string | undefined,
    minRating: search.minRating as number | undefined,
    maxRating: search.maxRating as number | undefined,
    menuItem: search.menuItem as string | undefined,
  }),
})

function RouteComponent() {
  const searchParams = Route.useSearch();
  const {getApiClient} = useAuth();
  const feedbackApi = getApiClient(AdminFeedbackApi);

  // Fetch all feedback data (no backend filtering)
  const {data: feedbackResponse, isLoading} = useQuery({
    queryKey: ['adminFeedback'],
    queryFn: async () => (await feedbackApi.getAllFeedback()).data,
    staleTime: 1000 * 60 * 5,
    refetchOnWindowFocus: false,
  });

  const feedbackList = feedbackResponse || [];

  // Build initial column filters from search params
  const buildInitialFilters = (): ColumnFiltersState => {
    const filters: ColumnFiltersState = [];
    
    if (searchParams.startDate && searchParams.endDate) {
      filters.push({
        id: 'createdAt',
        value: [searchParams.startDate, searchParams.endDate]
      });
    }
    
    if (searchParams.minRating !== undefined || searchParams.maxRating !== undefined) {
      filters.push({
        id: 'rating',
        value: [searchParams.minRating, searchParams.maxRating]
      });
    }
    
    if (searchParams.menuItem) {
      filters.push({
        id: 'menuItemName',
        value: searchParams.menuItem
      });
    }
    
    return filters;
  };

  // State for column filters with initial values from search params
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>(buildInitialFilters());

  // Update filters when search params change
  useEffect(() => {
    setColumnFilters(buildInitialFilters());
  }, [searchParams.startDate, searchParams.endDate, searchParams.minRating, searchParams.maxRating, searchParams.menuItem]);

  // Helper function to render star rating
  const renderStarRating = (rating?: number) => {
    if (!rating) return <span className="text-muted-foreground">No rating</span>;
    
    return (
      <div className="flex items-center gap-1">
        {[...Array(5)].map((_, index) => (
          <Star
            key={index}
            className={`h-4 w-4 ${
              index < rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
            }`}
          />
        ))}
        <span className="ml-1 text-sm font-medium">{rating}/5</span>
      </div>
    );
  };

  const columns: ColumnDef<any>[] = [
    {
      id: 'user',
      header: 'User',
      accessorFn: (row) => `${row?.user?.firstName} ${row?.user?.lastName} ${row.user?.email}`,
      cell: ({ row }) => {
        const user = row.original.user
        return (
          <div>
            <div className="font-medium">
              {user?.firstName} {user?.lastName}
            </div>
            <div className="text-sm text-muted-foreground">{user?.email || 'N/A'}</div>
          </div>
        );
      },
    },
    {
      accessorKey: 'menuItemName',
      header: 'Menu Item',
      cell: ({ row }) => {
        const menuItemName = row.getValue('menuItemName') as string | undefined;
        return <div className="font-medium">{menuItemName || 'N/A'}</div>;
      },
    },
    {
      accessorKey: 'rating',
      header: 'Rating',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => {
        const rating = row.getValue('rating') as number | undefined;
        return renderStarRating(rating);
      },
    },
    {
      accessorKey: 'comment',
      header: 'Comment',
      cell: ({ row }) => {
        const comment = row.getValue('comment') as string | undefined;
        return (
          <div className="max-w-md">
            {comment ? (
              <p className="text-sm line-clamp-2">{comment}</p>
            ) : (
              <span className="text-muted-foreground text-sm">No comment</span>
            )}
          </div>
        );
      },
    },
    {
      accessorKey: 'createdAt',
      header: 'Date',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: "date-range",
        processDate: true
      },
      cell: ({ row }) => formatDate(row.getValue('createdAt')),
    },
  ];

  return (
    <div className="space-y-4">
      <DataTable
        columns={columns}
        data={feedbackList}
        enableSorting
        enableFiltering
        pageSize={10}
        loading={isLoading}
        enableReporting
        reportTitle='Feedback Report'
        reportFilename='feedback'
        emptyMessage="No feedback found."
        className="space-y-4"
        searchPlaceholder="Search feedback..."
        columnFilters={columnFilters}
        setColumnFilters={setColumnFilters}
      />
    </div>
  );
}
