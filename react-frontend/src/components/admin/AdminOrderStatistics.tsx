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
                toast.error('No data available for the selected date');
                return;
            }

            // Prepare data for export
            const columns = ['Item Name', 'Quantity', 'Total Sales (R)'];
            const rows = reportData.items.map(item => [
                item.name || '',
                item.quantity || 0,
                `R ${(item.totalSales || 0).toFixed(2)}`
            ]);

            // Add summary row
            rows.push([
                'TOTAL:',
                reportData.totalQuantity || 0,
                `R ${(reportData.totalRevenue || 0).toFixed(2)}`
            ]);

            const filename = `kitchen-report-${date}`;

            if (format === 'csv') {
                generateCSVReport(columns, rows, filename);
            } else {
                generatePDFReport(columns, rows, filename, `Kitchen Report -${new Date(date).toLocaleDateString('en-ZA')}`);
            }

            toast.success('Report generated successfully');
        } catch (error) {
            console.error('Error generating kitchen report:', error);
            toast.error('Error generating kitchen report');
        }
    }

    const generateKitchenReportPeriod = async (format: 'csv' | 'pdf' = 'pdf') => {
        try {
            const response = await adminOrdersAPI.getKitchenReportPeriod(startDate, endDate, campusId, residenceId);
            const reportData = response.data;

            if (!reportData.items || reportData.items.length === 0) {
                toast.error('No data available for the selected period');
                return;
            }

            // Prepare data for export
            const columns = ['Item Name', 'Quantity', 'Total Sales (R)'];
            const rows = reportData.items.map(item => [
                item.name || '',
                item.quantity || 0,
                `R ${(item.totalSales || 0).toFixed(2)}`
            ]);

            // Add summary row
            rows.push([
                'TOTAL:',
                reportData.totalQuantity || 0,
                `R ${(reportData.totalRevenue || 0).toFixed(2)}`
            ]);

            const filename = `kitchen-report-period-${new Date(startDate).toLocaleDateString('en-ZA')}-${new Date(endDate).toLocaleDateString('en-ZA')}`;

            if (format === 'csv') {
                generateCSVReport(columns, rows, filename);
            } else {
                generatePDFReport(columns, rows, filename, `Kitchen Report -${formatDateLong(startDate)} to ${formatDateLong(endDate)}`);
            }

            toast.success('Report generated successfully');
        } catch (error) {
            console.error('Error generating kitchen report for period:', error);
            toast.error('Error generating kitchen report');
        }
    }

    const generateDeliveryReport = async (date: string, format: 'csv' | 'pdf' = 'pdf') => {
        try {
            const response = await adminOrdersAPI.getDeliveryReportData(date);
            const reportData = response.data;

            if (!reportData.deliveries || reportData.deliveries.length === 0) {
                toast.error('No data available for the selected date');
                return;
            }

            // Prepare data for export
            const columns = [
                'Delivery Date', 'Item Name', 'Quantity',
                'First Name', 'Last Name', 'Student Number', 'Residence', 'Campus'
            ];
            const rows = reportData.deliveries.map(delivery => [
                delivery.deliveryDate ? new Date(delivery.deliveryDate).toLocaleDateString('en-ZA') : '',
                delivery.itemName || '',
                delivery.quantity || 0,
                delivery.firstName || '',
                delivery.lastName || '',
                delivery.credentialNumber || '',
                delivery.residenceName || '',
                delivery.campusName || ''
            ]);

            const filename = `delivery-report-${date}`;

            if (format === 'csv') {
                generateCSVReport(columns, rows, filename);
            } else {
                generatePDFReport(columns, rows, filename, `Delivery Report -${new Date(date).toLocaleDateString('en-ZA')}`);
            }

            toast.success('Report generated successfully');
        } catch (error) {
            console.error('Error generating delivery report:', error);
            toast.error('Error generating delivery report');
        }
    }

    const generateDeliveryReportPeriod = async (format: 'csv' | 'pdf' = 'pdf') => {
        try {
            const response = await adminOrdersAPI.getDeliveryReportPeriod(startDate, endDate, campusId, residenceId);
            const reportData = response.data;

            if (!reportData.deliveries || reportData.deliveries.length === 0) {
                toast.error('No data available for the selected period');
                return;
            }

            // Prepare data for export
            const columns = [
                'Delivery Date', 'Item Name', 'Quantity',
                'First Name', 'Last Name', 'Student Number', 'Residence', 'Campus'
            ];
            const rows = reportData.deliveries.map(delivery => [
                delivery.deliveryDate ? new Date(delivery.deliveryDate).toLocaleDateString('en-ZA') : '',
                delivery.itemName || '',
                delivery.quantity || 0,
                delivery.firstName || '',
                delivery.lastName || '',
                delivery.credentialNumber || '',
                delivery.residenceName || '',
                delivery.campusName || ''
            ]);

            const filename = `delivery-report-period-${new Date(startDate).toLocaleDateString('en-ZA')}-${new Date(endDate).toLocaleDateString('en-ZA')}`;

            if (format === 'csv') {
                generateCSVReport(columns, rows, filename);
            } else {
                generatePDFReport(columns, rows, filename, `Delivery Report -${formatDateLong(startDate)} to ${formatDateLong(endDate)}`);
            }

            toast.success('Report generated successfully');
        } catch (error) {
            console.error('Error generating delivery report for period:', error);
            toast.error('Error generating delivery report');
        }
    }

    if (statistics.length === 0) {
        return (
            <Card>
                <CardContent className="p-6">
                    <p className="text-center text-muted-foreground">
                        No order statistics available
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
        const date = item.deliveryDate || 'Unknown Date';
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
                            Total Items
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
                            Total Revenue
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
                                Order Detail by Date
                            </h1>
                        </div>
                        <div className="flex flex-wrap gap-2">
                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <Button variant={'default'}>
                                        Kitchen Report
                                        <ChevronDownIcon className="ml-2 h-4 w-4" />
                                    </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent>
                                    <DropdownMenuItem onClick={() => generateKitchenReportPeriod('pdf')}>
                                        Download PDF
                                    </DropdownMenuItem>
                                    <DropdownMenuItem onClick={() => generateKitchenReportPeriod('csv')}>
                                        Download CSV
                                    </DropdownMenuItem>
                                </DropdownMenuContent>
                            </DropdownMenu>

                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <Button variant={'default'}>
                                        Delivery Report
                                        <ChevronDownIcon className="ml-2 h-4 w-4" />
                                    </Button>
                                </DropdownMenuTrigger>
                                <DropdownMenuContent>
                                    <DropdownMenuItem onClick={() => generateDeliveryReportPeriod('pdf')}>
                                        Download PDF
                                    </DropdownMenuItem>
                                    <DropdownMenuItem onClick={() => generateDeliveryReportPeriod('csv')}>
                                        Download CSV
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
                                                    {item.itemName || 'Unknown Item'}
                                                </div>
                                                <div className="text-sm text-muted-foreground flex gap-2">
                                                    <span>Quantity: {item.itemCount || 0}</span>
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
                                                    @R{item.itemCount ? ((item.totalRevenue || 0) / item.itemCount).toFixed(2) : '0.00'} each
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                    <div className='flex gap-2 mt-4'>
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant={'outline'}>
                                                    Kitchen Report
                                                    <ChevronDownIcon className="ml-2 h-4 w-4" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent>
                                                <DropdownMenuItem onClick={() => generateKitchenReport(date, 'pdf')}>
                                                    Download PDF
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={() => generateKitchenReport(date, 'csv')}>
                                                    Download CSV
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>

                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant={'outline'}>
                                                    Delivery Report
                                                    <ChevronDownIcon className="ml-2 h-4 w-4" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent>
                                                <DropdownMenuItem onClick={() => generateDeliveryReport(date, 'pdf')}>
                                                    Download PDF
                                                </DropdownMenuItem>
                                                <DropdownMenuItem onClick={() => generateDeliveryReport(date, 'csv')}>
                                                    Download CSV
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
