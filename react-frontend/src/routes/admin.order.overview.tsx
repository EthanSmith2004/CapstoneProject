import { AdminOrderManagementApi, ListApi, type SelectDTO } from '@/api';
import { AdminOrderStatistics } from '@/components/admin/AdminOrderStatistics';
import { AdminLoader } from '@/components/ui/adminLoader';
import { useAuth } from '@/contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router';
import { Label } from '@/components/ui/label';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from '@/components/ui/select';
import { Calendar } from '@/components/ui/calendar';
import { useState, useEffect } from 'react';
import { type DateRange } from 'react-day-picker';

export const Route = createFileRoute('/admin/order/overview')({
  component: RouteComponent,
})

interface StatisticPeriod {
  start: Date;
  end: Date;
  name: string;
}

function RouteComponent() {
  const predefinedPeriods: StatisticPeriod[] = [
    {
      start: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()),
      end: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() + 7),
      name: 'Volgende 7 dae',
    },
    {
      start: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()),
      end: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() + 30),
      name: 'Volgende 30 dae',
    },
    {
      start: new Date(new Date().getFullYear(), 0, 1),
      end: new Date(),
      name: 'Jaar tot op datum',
    },
    {
      start: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
      end: new Date(),
      name: 'Maand tot op datum',
    },
  ];

  const { getApiClient } = useAuth();
  const adminOrdersAPI = getApiClient(AdminOrderManagementApi);
  const listAPI = getApiClient(ListApi);
  const [statisticPeriod, setStatisticPeriod] = useState<StatisticPeriod>(predefinedPeriods[0]);
  const [dateRange, setDateRange] = useState<DateRange | undefined>();
  const [selectedCampus, setSelectedCampus] = useState<number | undefined>();
  const [selectedResidence, setSelectedResidence] = useState<number | undefined>();

  // Fetch campus and residence lists
  const { data: campusesResponse, isLoading: _campusesLoading } = useQuery({
    queryKey: ['admin-campuses-list'],
    queryFn: () => listAPI.getCampusNames(),
    staleTime: 1000 * 60 * 5, // 5 minutes
  });

  const { data: residencesResponse, isLoading: _residencesLoading } = useQuery({
    queryKey: ['admin-residences-list'],
    queryFn: () => listAPI.getResidenceNames(),
    staleTime: 1000 * 60 * 5, // 5 minutes
  });

  const campuses = campusesResponse?.data || [];
  const residences = residencesResponse?.data || [];

  const {data: orderSummaryData, isLoading: isOrderDataLoading} = useQuery({
    queryKey: ['adminOrderStats', statisticPeriod.start, statisticPeriod.end, selectedCampus, selectedResidence],
    queryFn: async () => {
      const response = await adminOrdersAPI.getOrderStatisticsPeriod(
        statisticPeriod.start.toISOString(),
        statisticPeriod.end.toISOString(),
        selectedCampus,
        selectedResidence
      );
      return response.data;
    },
    staleTime: 1000 * 60 * 5, // 5 minutes
  });

  useEffect(() => {
    if (dateRange?.from && dateRange?.to && 
      (dateRange.from != statisticPeriod.start || dateRange.to != statisticPeriod.end) &&
      (dateRange.from < dateRange.to)) {
      setStatisticPeriod({
        start: dateRange.from,
        end: dateRange.to,
        name: "Eie"
      });
    }
  }, [dateRange]);

  useEffect(() => {
    if (statisticPeriod.name !== "Eie") {
      setDateRange({
        from: statisticPeriod.start,
        to: statisticPeriod.end
      });
    }
  }, [statisticPeriod]);

  return (
    <div>
      <h2 className="text-2xl font-bold">Bestelling Oorsig</h2>
      <div className="p-4 flex flex-col lg:flex-row gap-6">
        <div className="space-y-4">
          <div>
            <Label htmlFor="period">Periode</Label>
            <Select onValueChange={(value) => {
              const period = predefinedPeriods.find(p => p.name === value);
              if (period) {
                setStatisticPeriod(period);
              }
            }} value={statisticPeriod.name}>
              <SelectTrigger id="period" className="w-[220px]">
                <SelectValue placeholder="Kies 'n periode" />
              </SelectTrigger>
              <SelectContent className='bg-white z-40'>
                <SelectItem value="Eie" disabled hidden>Eie</SelectItem>
                {predefinedPeriods.map((period) => (
                  <SelectItem key={period.name} value={period.name}>
                    {period.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div>
            <Label htmlFor="campus">Kampus (opsioneel)</Label>
            <Select 
              value={selectedCampus?.toString() || "all"} 
              onValueChange={(value) => {
                setSelectedCampus(value === "all" ? undefined : Number(value));
                // Reset residence when campus changes
                if (value === "all") {
                  setSelectedResidence(undefined);
                }
              }}
            >
              <SelectTrigger id="campus" className="w-[220px]">
                <SelectValue placeholder="Alle Kampusse" />
              </SelectTrigger>
              <SelectContent className='bg-white z-40'>
                <SelectItem value="all">Alle Kampusse</SelectItem>
                {campuses.map((campus: SelectDTO) => (
                  <SelectItem key={campus.id} value={campus.id?.toString() || ""}>
                    {campus.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div>
            <Label htmlFor="residence">Koshuis (opsioneel)</Label>
            <Select 
              value={selectedResidence?.toString() || "all"} 
              onValueChange={(value) => {
                setSelectedResidence(value === "all" ? undefined : Number(value));
              }}
            >
              <SelectTrigger id="residence" className="w-[220px]">
                <SelectValue placeholder="Alle Koshuise" />
              </SelectTrigger>
              <SelectContent className='bg-white z-40'>
                <SelectItem value="all">Alle Koshuise</SelectItem>
                {residences.map((residence: SelectDTO) => (
                  <SelectItem key={residence.id} value={residence.id?.toString() || ""}>
                    {residence.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <Calendar 
            mode="range"
            selected={dateRange} 
            defaultMonth={statisticPeriod.start}
            className="mb-4"
            onSelect={setDateRange}
            numberOfMonths={2}
            showOutsideDays={false}
            required
          />
        </div>
        <div className='flex-1'>
          {!isOrderDataLoading && orderSummaryData && (
            <AdminOrderStatistics 
              data={orderSummaryData} 
              startDate={statisticPeriod.start.toISOString()} 
              endDate={statisticPeriod.end.toISOString()}
              campusId={selectedCampus}
              residenceId={selectedResidence}
            />
          )}
          {isOrderDataLoading && <AdminLoader />}
        </div>
      </div>
    </div>
  )
}
