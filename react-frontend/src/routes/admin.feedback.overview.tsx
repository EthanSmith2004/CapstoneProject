import { AdminFeedbackApi } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { Label } from '@/components/ui/label';
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from '@/components/ui/select';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router'
import { Calendar } from '@/components/ui/calendar';
import { useState, useEffect } from 'react';
import { type DateRange } from 'react-day-picker';
import { AdminFeedbackStats } from '@/components/admin/AdminFeedbackStats';
import { AdminLoader } from '@/components/ui/adminLoader';

export const Route = createFileRoute('/admin/feedback/overview')({
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
  const feedbackApi = getApiClient(AdminFeedbackApi);
  const [statisticPeriod, setStatisticPeriod] = useState<StatisticPeriod>(predefinedPeriods[0]);
  const [dateRange, setDateRange] = useState<DateRange | undefined>();

  // Get current period statistics
  const {data: currentStats, isLoading: statsLoading} = useQuery({
    queryKey: ['feedbackStats', statisticPeriod.start, statisticPeriod.end],
    queryFn: async () => (await feedbackApi.getFeedbackStatistics(
      statisticPeriod.start.toISOString(), 
      statisticPeriod.end.toISOString()
    )).data,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });

  // Get previous period statistics for comparison
  const {data: previousStats} = useQuery({
    queryKey: ['feedbackStats', statisticPeriod.previousStart, statisticPeriod.previousEnd],
    queryFn: async () => {
      if (statisticPeriod.previousStart && statisticPeriod.previousEnd) {
        return (await feedbackApi.getFeedbackStatistics(
          statisticPeriod.previousStart.toISOString(), 
          statisticPeriod.previousEnd.toISOString()
        )).data;
      }
      return null;
    },
    enabled: !!(statisticPeriod.previousStart && statisticPeriod.previousEnd),
    staleTime: 1000 * 60 * 5,
  });

  // Get per-item statistics
  const {data: itemStats, isLoading: itemStatsLoading} = useQuery({
    queryKey: ['feedbackItemStats', statisticPeriod.start, statisticPeriod.end],
    queryFn: async () => (await feedbackApi.getFeedbackStatisticsByItem(
      statisticPeriod.start.toISOString(), 
      statisticPeriod.end.toISOString()
    )).data,
    staleTime: 1000 * 60 * 5,
  });

  // Update period when date range changes
  useEffect(() => {
    if (dateRange?.from && dateRange?.to && 
      (dateRange.from !== statisticPeriod.start || dateRange.to !== statisticPeriod.end) &&
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

  // Update date range when period changes
  useEffect(() => {
    if (statisticPeriod.name !== "Eie") {
      setDateRange({
        from: statisticPeriod.start,
        to: statisticPeriod.end
      });
    }
  }, [statisticPeriod]);

  const isLoading = statsLoading || itemStatsLoading;

  return (
    <div className="space-y-6">
      {/* Period Selector */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="md:col-span-1 space-y-4">
          <div>
            <Label htmlFor="period">Periode</Label>
            <Select
              value={statisticPeriod.name}
              onValueChange={(value) => {
                const period = predefinedPeriods.find(p => p.name === value);
                if (period) {
                  setStatisticPeriod(period);
                }
              }}
            >
              <SelectTrigger id="period">
                <SelectValue placeholder="Kies 'n periode" />
              </SelectTrigger>
              <SelectContent>
                {predefinedPeriods.map((period) => (
                  <SelectItem key={period.name} value={period.name}>
                    {period.name}
                  </SelectItem>
                ))}
                <SelectItem value="Eie">Eie</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Calendar for custom date range */}
          <div>
            <Label>Datum Reeks</Label>
            <Calendar
              mode="range"
              selected={dateRange}
              onSelect={setDateRange}
              className="rounded-md border"
              numberOfMonths={1}
            />
          </div>
        </div>

        <div className="md:col-span-2">
          {isLoading ? (
            <AdminLoader />
          ) : currentStats && itemStats ? (
            <AdminFeedbackStats
              currentStats={currentStats}
              previousStats={previousStats || undefined}
              itemStats={itemStats}
              startDate={statisticPeriod.start}
              endDate={statisticPeriod.end}
              contrastName={statisticPeriod.contrastName}
            />
          ) : (
            <div className="text-center py-12 text-muted-foreground">
              Geen data beskikbaar vir die geselekteerde periode nie.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
