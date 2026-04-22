import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { formatDateLong, formatDateNoTime } from '@/lib/utils';
import type { AdminOrderPeriodStatistics, BulkOrderStatusUpdateRequest } from '@/api/models';
import { Button } from '../ui/button';
import { useAuth } from '@/contexts/AuthContext';
import { AdminOrderManagementApi } from '@/api';
import { StatusEnum } from '@/api/models/bulk-order-status-update-request';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { orderItemStatusTranslations } from '@/data/enum-translations';
import { generateCSVReport, generatePDFReport } from '@/lib/reporting';
import { toast } from 'sonner';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '../ui/dropdown-menu';
import { ChevronDownIcon } from 'lucide-react';

interface AdminOrderStatisticsProps {
    data: AdminOrderPeriodStatistics;
    startDate: string;
    endDate: string;
    campusId?: number;
    residenceId?: number;
}

export function AdminOrderStatistics({ data, startDate, endDate, campusId, residenceId }: AdminOrderStatisticsProps) {
    const statistics = data?.statistics || [];

    const { getApiClient } = useAuth();
    const adminOrdersAPI = getApiClient(AdminOrderManagementApi);
    const queryClient = useQueryClient();

    const mutateItemStatus = useMutation({
        mutationFn: async (state: BulkOrderStatusUpdateRequest) => {
            return await adminOrdersAPI.bulkUpdateOrderStatuses(state);
        },
        onSuccess: () => {
            queryClient.refetchQueries({ predicate: (query) => {
                return query.queryKey.length >=1 && query.queryKey[0] === 'adminOrderStats';
            }});
        }
    });

    const generateKitchenReport = async (date: string, format: 'csv' | 'pdf' = 'pdf') => {
        try {
            const response = await adminOrdersAPI.getKitchenReportData(date);
            const reportData = response.data;

            if (!reportData.items || reportData.items.length === 0) {
                toast.error('Geen data beskikbaar vir die geselekteerde datum');
                return;
            }

            // Prepare data for export
            const columns = ['Item Naam', 'Hoeveelheid', 'Totale Verkope (R)'];
            const rows = reportData.items.map(item => [
                item.name || '',
                item.quantity || 0,
                `R ${(item.totalSales || 0).toFixed(2)}`
            ]);

            // Add summary row
            rows.push([
                'TOTAAL:',
                reportData.totalQuantity || 0,
                `R ${(reportData.totalRevenue || 0).toFixed(2)}`
            ]);

            const filename = `kombuis-verslag-${date}`;

            if (format === 'csv') {
                generateCSVReport(columns, rows, filename);
            } else {
                generatePDFReport(columns, rows, filename, `Kombuis Verslag - ${new Date(date).toLocaleDateString('af-ZA')}`);
            }

            toast.success('Verslag suksesvol gegenereer');
        } catch (error) {
            console.error('Error generating kitchen report:', error);
            toast.error('Fout met die generering van kombuis verslag');
        }
    }

    const generateKitchenReportPeriod = async (format: 'csv' | 'pdf' = 'pdf') => {
        try {
            const response = await adminOrdersAPI.getKitchenReportPeriod(startDate, endDate, campusId, residenceId);
            const reportData = response.data;

            if (!reportData.items || reportData.items.length === 0) {
                toast.error('Geen data beskikbaar vir die geselekteerde periode');
                return;
            }

            // Prepare data for export
            const columns = ['Item Naam', 'Hoeveelheid', 'Totale Verkope (R)'];
            const rows = reportData.items.map(item => [
                item.name || '',
                item.quantity || 0,
                `R ${(item.totalSales || 0).toFixed(2)}`
            ]);

            // Add summary row
            rows.push([
                'TOTAAL:',
                reportData.totalQuantity || 0,
                `R ${(reportData.totalRevenue || 0).toFixed(2)}`
            ]);

            const filename = `kombuis-verslag-periode-${new Date(startDate).toLocaleDateString('af-ZA')}-${new Date(endDate).toLocaleDateString('af-ZA')}`;

            if (format === 'csv') {
                generateCSVReport(columns, rows, filename);
            } else {
                generatePDFReport(columns, rows, filename, `Kombuis Verslag - ${formatDateLong(startDate)} tot ${formatDateLong(endDate)}`);
            }

            toast.success('Verslag suksesvol gegenereer');
        } catch (error) {
            console.error('Error generating kitchen report for period:', error);
            toast.error('Fout met die generering van kombuis verslag');
        }
    }

    const generateDeliveryReport = async (date: string, format: 'csv' | 'pdf' = 'pdf') => {
        try {
            const response = await adminOrdersAPI.getDeliveryReportData(date);
            const reportData = response.data;

            if (!reportData.deliveries || reportData.deliveries.length === 0) {
                toast.error('Geen data beskikbaar vir die geselekteerde datum');
                return;
            }

            // Prepare data for export
            const columns = [
                'Aflewering Datum', 'Item Naam', 'Hoeveelheid',
                'Voornaam', 'Van', 'Student Nommer', 'Koshuis', 'Kampus'
            ];
            const rows = reportData.deliveries.map(delivery => [
                delivery.deliveryDate ? new Date(delivery.deliveryDate).toLocaleDateString('af-ZA') : '',
                delivery.itemName || '',
                delivery.quantity || 0,
                delivery.firstName || '',
                delivery.lastName || '',
                delivery.credentialNumber || '',
                delivery.residenceName || '',
                delivery.campusName || ''
            ]);

            const filename = `aflewering-verslag-${date}`;

            if (format === 'csv') {
                generateCSVReport(columns, rows, filename);
            } else {
                generatePDFReport(columns, rows, filename, `Aflewering Verslag - ${new Date(date).toLocaleDateString('af-ZA')}`);
            }

            toast.success('Verslag suksesvol gegenereer');
        } catch (error) {
            console.error('Error generating delivery report:', error);
            toast.error('Fout met die generering van aflewering verslag');
        }
    }

    const generateDeliveryReportPeriod = async (format: 'csv' | 'pdf' = 'pdf') => {
        try {
            const response = await adminOrdersAPI.getDeliveryReportPeriod(startDate, endDate, campusId, residenceId);
            const reportData = response.data;

            if (!reportData.deliveries || reportData.deliveries.length === 0) {
                toast.error('Geen data beskikbaar vir die geselekteerde periode');
                return;
            }

            // Prepare data for export
            const columns = [
                'Aflewering Datum', 'Item Naam', 'Hoeveelheid',
                'Voornaam', 'Van', 'Student Nommer', 'Koshuis', 'Kampus'
            ];
            const rows = reportData.deliveries.map(delivery => [
                delivery.deliveryDate ? new Date(delivery.deliveryDate).toLocaleDateString('af-ZA') : '',
                delivery.itemName || '',
                delivery.quantity || 0,
                delivery.firstName || '',
                delivery.lastName || '',
                delivery.credentialNumber || '',
                delivery.residenceName || '',
                delivery.campusName || ''
            ]);

            const filename = `aflewering-verslag-periode-${new Date(startDate).toLocaleDateString('af-ZA')}-${new Date(endDate).toLocaleDateString('af-ZA')}`;

            if (format === 'csv') {
                generateCSVReport(columns, rows, filename);
            } else {
                generatePDFReport(columns, rows, filename, `Aflewering Verslag - ${formatDateLong(startDate)} tot ${formatDateLong(endDate)}`);
            }

            toast.success('Verslag suksesvol gegenereer');
        } catch (error) {
            console.error('Error generating delivery report for period:', error);
            toast.error('Fout met die generering van aflewering verslag');
        }
    }

    if (statistics.length === 0) {
        return (
            <Card>
                <CardContent className="p-6">
                    <p className="text-center text-muted-foreground">
                        Geen bestelling statistieke beskikbaar nie
                    </p>
                </CardContent>
            </Card>
        );
    }

    const statusOptions = Object.entries(StatusEnum).map(([_, value]) => ({
        value: value as string,
        label: value as string
    }));

    // Calculate totals for the period
    const totalRevenue = statistics.reduce(
        (sum, item) => sum + (item.totalRevenue || 0),
        0
    );

    const totalItems = statistics.reduce(
        (sum, item) => sum + (item.itemCount || 0),
        0
    );

    // Group by delivery date
    const groupedByDate = statistics.reduce((acc, item) => {
        const date = item.deliveryDate || 'Onbekende Datum';
        if (!acc[date]) {
            acc[date] = [];
        }
        acc[date].push(item);
        return acc;
    }, {} as Record<string, typeof statistics>);

    return (
        <div className="space-y-6">
            {/* Prominent Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <Card className="gap-0 bg-gray-50">
                    <CardHeader className="">
                        <CardTitle className="text-xl font-medium text-muted-foreground">
                            Totale Items
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-4xl font-bold text-blue-600 pb-2">{totalItems}</div>
                        <p className="text-xs text-muted-foreground">
                            {formatDateNoTime(startDate)} - {formatDateNoTime(endDate)}
                        </p>
                    </CardContent>
                </Card>
                <Card className='gap-0 bg-gray-50'>
                    <CardHeader className="">
                        <CardTitle className="text-xl font-medium text-muted-foreground">
                            Totale Inkomste
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="text-4xl font-bold text-green-600 pb-2">
                            R{totalRevenue.toFixed(2)}
                        </div>
                        <p className="text-xs text-muted-foreground">
                            {formatDateNoTime(startDate)} - {formatDateNoTime(endDate)}
                        </p>
                    </CardContent>
                </Card>
            </div>

            <Card className="w-full bg-gray-50">
                <CardHeader>
                    <CardTitle className="flex justify-between items-center">
                        <div>
                            <h1 className='text-lg'>
                                Bestelling Detail per Datum
                            </h1>
                        </div>
                        <div className="flex flex-wrap gap-2">
                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <Button variant={'default'}>
                                        Kombuis Verslag
                                        <ChevronDownIcon className="ml-2 h-4 w-4" />
                                    </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent>
                                    <DropdownMenuItem onClick={() => generateKitchenReportPeriod('pdf')}>
                                        Laai PDF af
                                    </DropdownMenuItem>
                                    <DropdownMenuItem onClick={() => generateKitchenReportPeriod('csv')}>
                                        Laai CSV af
                                    </DropdownMenuItem>
                                </DropdownMenuContent>
                            </DropdownMenu>

                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <Button variant={'default'}>
                                        Aflewering Verslag
                                        <ChevronDownIcon className="ml-2 h-4 w-4" />
                                    </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent>
                                    <DropdownMenuItem onClick={() => generateDeliveryReportPeriod('pdf')}>
                                        Laai PDF af
                                    </DropdownMenuItem>
                                    <DropdownMenuItem onClick={() => generateDeliveryReportPeriod('csv')}>
                                        Laai CSV af
                                    </DropdownMenuItem>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        </div>
                    </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                    {Object.entries(groupedByDate).map(([date, items]) => {
                        const dateRevenue = items.reduce(
                            (sum, item) => sum + (item.totalRevenue || 0),
                            0
                        );
                        const dateItemCount = items.reduce(
                            (sum, item) => sum + (item.itemCount || 0),
                            0
                        );

                        return (
                            <div key={date} className="border rounded-lg p-4">
                                <div className="flex justify-between items-center mb-3">
                                    <h4 className="font-semibold text-md">
                                        {formatDateLong(date)}
                                    </h4>
                                    <div className="flex gap-2">
                                        <Badge variant="outline">
                                            {dateItemCount} Items
                                        </Badge>
                                        <Badge>
                                            R{dateRevenue.toFixed(2)}
                                        </Badge>
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    {items.map((item, index) => (
                                        <div
                                            key={`${item.itemName}-${index}`}
                                            className="flex justify-between items-center p-3 bg-muted/50 rounded-md"
                                        >
                                            <div className="flex-1">
                                                <div className="font-medium">
                                                    {item.itemName || 'Onbekende Item'}
                                                </div>
                                                <div className="text-sm text-muted-foreground flex gap-2">
                                                    <span>Hoeveelheid: {item.itemCount || 0}</span>
                                                </div>
                                                {item.itemStatus && (
                                                    <Select value={item.itemStatus} onValueChange={(value) => mutateItemStatus.mutate(
                                                        {
                                                            itemName: item.itemName || '',
                                                            deliveryDate: item.deliveryDate || '',
                                                            status: value as StatusEnum,
                                                            previousStatus: item.itemStatus as StatusEnum,
                                                        }
                                                    )}>
                                                        <SelectTrigger className="w-[180px] font-medium" size='sm'>
                                                            <SelectValue placeholder="Status" />
                                                        </SelectTrigger>
                                                        <SelectContent>
                                                            {statusOptions.map((option) => (
                                                                <SelectItem key={option.value} value={option.value}>
                                                                    {orderItemStatusTranslations[option.label]}
                                                                </SelectItem>
                                                            ))}
                                                        </SelectContent>
                                                    </Select>
                                                )}
                                            </div>
                                            <div className="text-right">
                                                <div className="font-semibold">
                                                    R{(item.totalRevenue || 0).toFixed(2)}
                                                </div>
                                                <div className="text-sm text-muted-foreground">
                                                    @R{item.itemCount ? ((item.totalRevenue || 0) / item.itemCount).toFixed(2) : '0.00'} elk
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                    <div className='flex gap-2 mt-4'>
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant={'outline'}>
                                                    Kombuis Verslag
                                                    <ChevronDownIcon className="ml-2 h-4 w-4" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent>
                                                <DropdownMenuItem onClick={() => generateKitchenReport(date, 'pdf')}>
                                                    Laai PDF af
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={() => generateKitchenReport(date, 'csv')}>
                                                    Laai CSV af
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>

                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant={'outline'}>
                                                    Aflewering Verslag
                                                    <ChevronDownIcon className="ml-2 h-4 w-4" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent>
                                                <DropdownMenuItem onClick={() => generateDeliveryReport(date, 'pdf')}>
                                                    Laai PDF af
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={() => generateDeliveryReport(date, 'csv')}>
                                                    Laai CSV af
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </CardContent>
            </Card>
        </div>
    );
}