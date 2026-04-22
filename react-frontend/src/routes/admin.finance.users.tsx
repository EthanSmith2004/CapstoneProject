import { AdminFinanceApi, type FinanceUserDTO } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute, Link } from '@tanstack/react-router'
import { type ColumnDef, type ColumnFiltersState } from '@tanstack/react-table'
import { DataTable } from '@/components/table/Table'
import { useState, useEffect, useCallback } from 'react';
import { FileText, User, TrendingUp, TrendingDown } from 'lucide-react'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'

interface SearchParams {
  userId?: number;
}

export const Route = createFileRoute('/admin/finance/users')({
  component: RouteComponent,
  validateSearch: (search: Record<string, unknown>): SearchParams => ({
    userId: search.userId as number | undefined,
  }),
})

function RouteComponent() {
  const searchParams = Route.useSearch()
  const { getApiClient, user } = useAuth();
  const financeAPI = getApiClient(AdminFinanceApi);
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([])
  const [selectedMetric, setSelectedMetric] = useState<'balance' | 'movementDay' | 'movementWeek' | 'movementMonth'>('balance');

  // Memoize the metric change handler to prevent infinite loops
  const handleMetricChange = useCallback((value: 'balance' | 'movementDay' | 'movementWeek' | 'movementMonth') => {
    setSelectedMetric(value);
  }, []);

  // Set initial filter when userId search param is present
  useEffect(() => {
    if (searchParams.userId) {
      setColumnFilters([{ id: 'id', value: searchParams.userId.toString() }])
    }
  }, [searchParams.userId])

  const { data: users, isLoading: isUsersLoading } = useQuery({
    queryKey: ['financeUsers'],
    queryFn: async () => (await financeAPI.findUsers()).data,
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  });

  // Format amount to display with 2 decimal places and proper currency symbol
  const formatAmount = (amount?: number) => {
    if (amount === undefined) return 'R0.00';
    return new Intl.NumberFormat('en-ZA', { 
      style: 'currency', 
      currency: 'ZAR',
      minimumFractionDigits: 2 
    }).format(amount);
  };

  // Check if user has the required roles
  const hasFinanceRole = user?.roles.includes('ROLE_FINANCIAL_ADMIN') || user?.roles.includes('ROLE_ADMIN');
  const hasUserAdminRole = user?.roles.includes('ROLE_USER_ADMIN') || user?.roles.includes('ROLE_ADMIN');

  // Intelligent range calculation
  const calculateRanges = (data: FinanceUserDTO[], metricKey: 'balance' | 'movementDay' | 'movementWeek' | 'movementMonth') => {
    if (!data || data.length === 0) return [];

    const values = data.map(u => u[metricKey] || 0);
    const min = Math.min(...values);
    const max = Math.max(...values);
    const range = max - min;

    // Determine best interval size (R10, R20, R50, R100, R200, R500, R1000)
    const possibleIntervals = [10, 20, 50, 100, 200, 500, 1000];
    let interval = 100; // default

    for (const testInterval of possibleIntervals) {
      const categoryCount = Math.ceil(range / testInterval);
      if (categoryCount >= 5 && categoryCount <= 10) {
        interval = testInterval;
        break;
      }
    }

    // If still too many or too few categories, adjust
    if (Math.ceil(range / interval) > 10) {
      interval = possibleIntervals.find(testInt => Math.ceil(range / testInt) <= 10) || 1000;
    } else if (Math.ceil(range / interval) < 5 && interval > 10) {
      const reversed = [...possibleIntervals].reverse();
      interval = reversed.find(testInt => Math.ceil(range / testInt) >= 5) || 10;
    }

    const ranges: { label: string; min: number; max: number; isNegative: boolean }[] = [];
    
    // Start from the lowest value (including negatives)
    const startValue = Math.floor(min / interval) * interval;
    let currentMin = startValue;
    
    while (currentMin < max) {
      const currentMax = currentMin + interval;
      const isNegativeRange = currentMax <= 0;
      
      if (currentMax >= max && currentMax - max < interval * 0.3) {
        // Last range, extend to include max
        ranges.push({ 
          label: currentMin < 0 ? `-R${Math.abs(currentMin)}+` : `R${currentMin}+`, 
          min: currentMin, 
          max: Infinity,
          isNegative: isNegativeRange
        });
        break;
      } else {
        // Format label based on whether range is negative or positive
        let label: string;
        if (currentMax <= 0) {
          // Fully negative range
          label = `-R${Math.abs(currentMin)} - -R${Math.abs(currentMax)}`;
        } else if (currentMin < 0 && currentMax > 0) {
          // Range crosses zero
          label = `-R${Math.abs(currentMin)} - R${currentMax}`;
        } else {
          // Fully positive range
          label = `R${currentMin} - R${currentMax}`;
        }
        
        ranges.push({ 
          label, 
          min: currentMin, 
          max: currentMax,
          isNegative: isNegativeRange
        });
      }
      currentMin = currentMax;
    }

    return ranges;
  };

  const columns: ColumnDef<FinanceUserDTO>[] = [
    {
      accessorKey: 'id',
      header: 'ID',
      enableColumnFilter: true,
      enableGlobalFilter: false,
      filterFn: 'equalsString',
      meta: {
        filterVariant: 'string',
      },
      cell: ({ row }) => <div>{row.getValue('id')}</div>,
    },
    {
      id: 'user',
      accessorFn: (r) => `${r.firstName} ${r.lastName} ${r.email} ${r.credentialNumber}`,
      header: 'Gebruiker',
      cell: ({ row }) => {
        const userData = row.original
        return (
          <div>
            <div className="font-medium">
              {userData.firstName} {userData.lastName}
            </div>
            <div className="text-sm text-muted-foreground">{userData.email || 'N/A'}</div>
            <div className="text-sm text-muted-foreground">{userData.credentialNumber || 'N/A'}</div>
          </div>
        );
      },
    },
    {
      accessorKey: 'balance',
      header: 'Balans',
      filterFn: 'inNumberRange',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => {
        const balance = row.getValue('balance') as number | undefined;
        const isNegative = (balance || 0) < 0;
        return (
          <div className={isNegative ? "text-destructive font-medium text-right" : "text-green-600 font-medium text-right"}>
            {formatAmount(balance)}
          </div>
        );
      },
    },
    {
      accessorKey: 'movementDay',
      header: 'Beweging (Dag)',
      filterFn: 'inNumberRange',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => {
        const movement = row.getValue('movementDay') as number | undefined;
        const isNegative = (movement || 0) < 0;
        return (
          <div className={`text-right font-medium ${isNegative ? 'text-destructive' : 'text-green-600'}`}>
            {formatAmount(movement)}
          </div>
        );
      },
    },
    {
      accessorKey: 'movementWeek',
      header: 'Beweging (Week)',
      filterFn: 'inNumberRange',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => {
        const movement = row.getValue('movementWeek') as number | undefined;
        const isNegative = (movement || 0) < 0;
        return (
          <div className={`text-right font-medium ${isNegative ? 'text-destructive' : 'text-green-600'}`}>
            {formatAmount(movement)}
          </div>
        );
      },
    },
    {
      accessorKey: 'movementMonth',
      header: 'Beweging (Maand)',
      filterFn: 'inNumberRange',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => {
        const movement = row.getValue('movementMonth') as number | undefined;
        const isNegative = (movement || 0) < 0;
        return (
          <div className={`text-right font-medium ${isNegative ? 'text-destructive' : 'text-green-600'}`}>
            {formatAmount(movement)}
          </div>
        );
      },
    },
    {
      id: 'actions',
      header: 'Aksies',
      cell: ({ row }) => {
        const userData = row.original;
        const userId = userData.id;
        const userEmail = userData.email;
        
        return (
          <div className="flex gap-2">
            {hasUserAdminRole && userId && (
              <Link
                to="/admin/user"
                search={{ userId }}
                className="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors hover:bg-accent hover:text-accent-foreground h-9 px-3"
                title="Bekyk gebruiker"
              >
                <User className="h-4 w-4" />
              </Link>
            )}
            {hasFinanceRole && userEmail && (
              <Link
                to="/admin/finance/transactions"
                search={{ userEmail }}
                className="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors hover:bg-accent hover:text-accent-foreground h-9 px-3"
                title="Bekyk transaksies"
              >
                <FileText className="h-4 w-4" />
              </Link>
            )}
          </div>
        );
      },
    },
  ];

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Finansiële Gebruikers</h1>
        <p className="text-muted-foreground">
          Bekyk alle gebruikers met hulle rekeningbalanse en beweging
        </p>
      </div>

      <div className="mb-6 grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Statistics Cards */}
        <div className="rounded-lg border p-4">
          <div className="grid grid-cols-1 gap-4">
            <div className="bg-muted p-4 rounded-lg">
              <div className="text-sm text-muted-foreground">Totale Gebruikers</div>
              <div className="text-2xl font-bold">{users?.length || 0}</div>
            </div>
            <div className="bg-muted p-4 rounded-lg">
              <div className="text-sm text-muted-foreground">Gemiddelde Balans</div>
              <div className="text-2xl font-bold">
                {formatAmount(
                  (users && users.length > 0)
                    ? users.reduce((sum, u) => sum + (u.balance || 0), 0) / users.length
                    : 0
                )}
              </div>
            </div>
            <div className="bg-muted p-4 rounded-lg">
              <div className="text-sm text-muted-foreground">Hoogste Balans</div>
              <div className="text-2xl font-bold text-green-600">
                {formatAmount(
                  Math.max(...(users || []).map(u => u.balance || 0))
                )}
              </div>
              <Button
                variant="outline"
                size="sm"
                className="mt-2 w-full"
                onClick={() => {
                  const maxBalance = Math.max(...(users || []).map(u => u.balance || 0));
                  const userWithMax = (users || []).find(u => u.balance === maxBalance);
                  if (userWithMax?.id) {
                    setColumnFilters([{ id: 'id', value: userWithMax.id.toString() }]);
                  }
                }}
              >
                <TrendingUp className="h-4 w-4 mr-2" />
                Bekyk Gebruiker
              </Button>
            </div>
            <div className="bg-muted p-4 rounded-lg">
              <div className="text-sm text-muted-foreground">Laagste Balans</div>
              <div className="text-2xl font-bold text-destructive">
                {formatAmount(
                  Math.min(...(users || []).map(u => u.balance || 0))
                )}
              </div>
              <Button
                variant="outline"
                size="sm"
                className="mt-2 w-full"
                onClick={() => {
                  const minBalance = Math.min(...(users || []).map(u => u.balance || 0));
                  const userWithMin = (users || []).find(u => u.balance === minBalance);
                  if (userWithMin?.id) {
                    setColumnFilters([{ id: 'id', value: userWithMin.id.toString() }]);
                  }
                }}
              >
                <TrendingDown className="h-4 w-4 mr-2" />
                Bekyk Gebruiker
              </Button>
            </div>
          </div>
        </div>

        {/* Balance Distribution Histogram */}
        <div className="lg:col-span-2 rounded-lg border p-4">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-semibold">Verspreiding</h3>
            <div className="flex items-center gap-2">
              <Label className="text-sm font-medium">Veranderlike:</Label>
              <Select value={selectedMetric} onValueChange={handleMetricChange}>
                <SelectTrigger className="w-[200px]">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="balance">Balans</SelectItem>
                  <SelectItem value="movementDay">Beweging (Dag)</SelectItem>
                  <SelectItem value="movementWeek">Beweging (Week)</SelectItem>
                  <SelectItem value="movementMonth">Beweging (Maand)</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <div className="space-y-2">
            {(() => {
              const dataToUse = users || [];
              const ranges = calculateRanges(dataToUse, selectedMetric);

              const distribution = ranges.map(range => ({
                ...range,
                count: dataToUse.filter((u: FinanceUserDTO) => 
                  (u[selectedMetric] || 0) >= range.min && (u[selectedMetric] || 0) < range.max
                ).length
              }));

              const maxCount = Math.max(...distribution.map(d => d.count), 1);

              return distribution.map(range => {
                const percentage = (range.count / maxCount) * 100;
                
                return (
                  <div key={range.label} className="flex items-center gap-3">
                    <span className="text-sm font-medium w-40">{range.label}</span>
                    <div className="flex-1 bg-gray-200 rounded-full h-6">
                      <div 
                        className={`h-6 rounded-full transition-all ${
                          range.isNegative ? 'bg-destructive' : 'bg-green-600'
                        }`}
                        style={{ width: `${percentage}%` }}
                      />
                    </div>
                    <span className="text-sm font-medium w-12 text-right">
                      {range.count}
                    </span>
                  </div>
                );
              });
            })()}
          </div>
        </div>
      </div>

      <DataTable
        columns={columns}
        data={users || []}
        enableSorting
        enableFiltering
        pageSize={25}
        loading={isUsersLoading}
        enableReporting
        reportTitle='Finansiële Gebruikers Verslag'
        reportFilename='finansiele-gebruikers'
        emptyMessage="Geen gebruikers gevind nie."
        columnFilters={columnFilters}
        setColumnFilters={setColumnFilters}
      />
    </div>
  )
}
