import { AdminFinanceApi, type AdminFinanceLoadResponse } from '@/api';
import { createFileRoute } from '@tanstack/react-router';
import { useAuth } from '@/contexts/AuthContext';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';

import { Button } from '@/components/ui/button';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Alert, AlertTitle } from '@/components/ui/alert';
import { Check, AlertTriangle, Upload, FileText } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import type { AxiosError } from 'axios';

export const Route = createFileRoute('/admin/finance/load-bulk')({
  component: RouteComponent,
})

const formSchema = z.object({
  csvFile: z.instanceof(File, { message: "Please select a CSV file" }),
});

function RouteComponent() {
  const { getApiClient } = useAuth();
  const financeAPI = getApiClient(AdminFinanceApi);
  const [loadSuccess, setLoadSuccess] = useState(false);
  const [result, setResult] = useState<AdminFinanceLoadResponse | null>(null);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {},
  });

  const bulkLoadMutation = useMutation({
    mutationFn: (data: File) => financeAPI.bulkLoadCredit(data),
    onSuccess: (response) => {
      setLoadSuccess(true);
      setResult(response.data);
      form.reset();
      // Hide success message after 5 seconds
      setTimeout(() => setLoadSuccess(false), 5000);
    },
    onError: (error: AxiosError) => {
      const result = error.response?.data
      if(result) {
        setResult(result);
      }
    }
  });

  const handleSubmit = (values: z.infer<typeof formSchema>) => {
    bulkLoadMutation.mutate(values.csvFile);
  };

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Bulk Credit Load</h1>
        <p className="text-muted-foreground">
          Load credit for multiple users using a CSV file
        </p>
      </div>

      {loadSuccess && (
        <Alert className="mb-6 bg-green-50 border-green-500">
          <Check className="h-4 w-4 text-green-500" />
          <AlertTitle className="text-green-800">Success</AlertTitle>
          <div className="text-green-700">
            <p>Credit loaded successfully:</p>
            <ul className="list-disc list-inside mt-2">
              <li>Successful transactions: {result?.successfulLoads || 0}</li>
              <li>Failed transactions: {result?.failedLoads || 0}</li>
              {result?.totalAmountLoaded && <li>Total amount loaded: R{result?.totalAmountLoaded.toFixed(2)}</li>}
            </ul>
          </div>
        </Alert>
      )}

      {bulkLoadMutation.isError && (
        <Alert className="mb-6 bg-red-50 border-red-500">
          <AlertTriangle className="h-4 w-4 text-red-500" />
          <AlertTitle className="text-red-800">Error</AlertTitle>
          <div className="text-red-700">
            There was a problem loading credit. Make sure your CSV file is formatted correctly.
          </div>
          Failed transactions: {result?.failedLoads || 0}
          <ul className='list-disc list-inside'>
            {result?.results?.filter((v) => !v.success).map((r) => (
                <li key={r.identifier}>{r.identifier} - {r.errorMessage}</li>
              )
            )}
          </ul>
        </Alert>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card className='bg-background'>
          <CardHeader>
            <CardTitle>Load CSV File</CardTitle>
            <CardDescription>
              Upload a CSV file with user information and amounts
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
                <FormField
                  control={form.control}
                  name="csvFile"
                  render={({ field: { onChange, value, ...rest } }) => (
                    <FormItem>
                      <FormLabel>CSV File</FormLabel>
                      <FormControl>
                        <div className="flex items-center gap-2">
                          <Input
                            type="file"
                            accept=".csv"
                            onChange={(e) => {
                              if (e.target.files?.[0]) {
                                onChange(e.target.files[0]);
                              }
                            }}
                            {...rest}
                          />
                        </div>
                      </FormControl>
                      <FormMessage />
                      {value && (
                        <div className="text-sm text-muted-foreground mt-2 flex items-center gap-2">
                          <FileText className="h-4 w-4" />
                          {(value as File).name} ({Math.round((value as File).size / 1024)} KB)
                        </div>
                      )}
                    </FormItem>
                  )}
                />
                
                <Button
                  type="submit"
                  disabled={form.formState.isSubmitting || bulkLoadMutation.isPending}
                >
                  {bulkLoadMutation.isPending ? (
                    'Loading...'
                  ) : (
                    <div className="flex items-center gap-2">
                      <Upload className="h-4 w-4" />
                      Upload CSV
                    </div>
                  )}
                </Button>
              </form>
            </Form>
          </CardContent>
        </Card>

        <Card className='bg-background'>
          <CardHeader>
            <CardTitle>CSV Format</CardTitle>
            <CardDescription>
              How to prepare your CSV file
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <p>
              The CSV file must be in the following format:
            </p>
            
            <div className="bg-muted p-3 rounded-md font-mono text-sm overflow-x-auto">
              email,credential,amount,description<br />
              user1@example.com,,100.00,"Monthly Credit"<br />
              ,202413312,50.00,"Extra Credit"<br />
              ...
            </div>
            
            <div className="space-y-2">
              <p className="font-medium">Important notes:</p>
              <ul className="list-disc list-inside text-sm space-y-1">
                <li>The file must contain a header row as shown above</li>
                <li>Each row must contain a valid email address or student/staff number, amount, and description</li>
                <li>Amounts must be numeric (use dots for decimals, not commas)</li>
                <li>Descriptions containing commas must be wrapped in quotation marks</li>
                <li>The maximum file size is 5MB</li>
              </ul>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
