import { AdminMenuApi } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { Label } from '@/components/ui/label';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from '@/components/ui/select';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router'
import { Calendar } from '@/components/ui/calendar';
import { useState, useEffect } from 'react';
import { type DateRange} from 'react-day-picker';
import { AdminMenuStats } from '@/components/admin/AdminMenuStats';
import { AdminLoader } from '@/components/ui/adminLoader';

export const Route = createFileRoute('/admin/menu/stats')({
  component: RouteComponent,
})

interface StatisticPeriod {
  start: Date;
  end: Date;
  name: string;
  previousStart?: Date;
  previousEnd?: Date;
  contrastName?: string;
}

function RouteComponent() {
  const predefinedPeriods: StatisticPeriod[] = [
    {
      start: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() - 7),
      end: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate(), 23, 59, 59),
      previousStart: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() - 14),
      previousEnd: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() - 8, 23, 59, 59),
      name: 'Laaste 7 dae',
      contrastName: 'Vorige 7 dae',
    },
    {
      start: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() - 30),
      end: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate(), 23, 59, 59),
      previousStart: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() - 60),
      previousEnd: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() - 31, 23, 59, 59),
      name: 'Laaste 30 dae',
      contrastName: 'Vorige 30 dae',
    },
    {
      start: new Date(new Date().getFullYear(), 0, 1),
      end: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate(), 23, 59, 59),
      name: 'Jaar tot datum',
    },
    {
      start: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
      end: new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate(), 23, 59, 59),
      name: 'Maand tot op datum',
    },
  ];

  const {getApiClient} = useAuth();
  const menuAPI = getApiClient(AdminMenuApi);
  const [statisticPeriod, setStatisticPeriod] = useState<StatisticPeriod>(predefinedPeriods[0]);
  const [dateRange, setDateRange] = useState<DateRange | undefined>();

  const {data: menuStatistics, isLoading: menuStatisticsLoading} = useQuery({
    queryKey: ['menuStatistics', statisticPeriod.start, statisticPeriod.end],
    queryFn: async () => (await menuAPI.getMenuItemStatistics(statisticPeriod.start.toISOString(), statisticPeriod.end.toISOString())).data,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });

  const {data: previousMenuStatistics} = useQuery({
    queryKey: ['menuStatistics', statisticPeriod.previousStart, statisticPeriod.previousEnd],
    queryFn: async () => {
      if (statisticPeriod.previousStart && statisticPeriod.previousEnd) {
        return (await menuAPI.getMenuItemStatistics(statisticPeriod.previousStart.toISOString(), statisticPeriod.previousEnd.toISOString())).data;
      }
      return null;
    },
    enabled: !!(statisticPeriod.previousStart && statisticPeriod.previousEnd),
    staleTime: 1000 * 60 * 5, // 5 minutes
  });

  useEffect(() => {
    if (dateRange?.from && dateRange?.to && 
      (dateRange.from != statisticPeriod.start || dateRange.to != statisticPeriod.end) &&
      (dateRange.from < dateRange.to)) {
      setStatisticPeriod({
        start: dateRange.from,
        end: dateRange.to,
        previousStart: undefined,
        previousEnd: undefined,
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
    <div className="p-4 flex flex-col lg:flex-row">
      <div>
        <Label htmlFor="period">Periode</Label>
        <Select onValueChange={(value) => {
          const period = predefinedPeriods.find(p => p.name === value);
          if (period) {
            setStatisticPeriod(period);
          }
        }} value={statisticPeriod.name}>
          <SelectTrigger id="period" className="w-[180px] mb-4">
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
        <Calendar 
          mode="range"
          selected={dateRange} 
          defaultMonth={statisticPeriod.start}
          disabled={{after: new Date()}}
          className="mb-4"
          onSelect={setDateRange}
          numberOfMonths={2}
          showOutsideDays={false}
          required
        />
      </div>
      <div className='ml-8 flex-1'>
        {!menuStatisticsLoading && menuStatistics && <AdminMenuStats 
          stats={menuStatistics} 
          previousStats={previousMenuStatistics}
          contrastName={statisticPeriod.contrastName}
        />}
        {menuStatisticsLoading && <AdminLoader />}
      </div>
    </div>
  );
}
