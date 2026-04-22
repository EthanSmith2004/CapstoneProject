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
  csvFile: z.instanceof(File, { message: "Kies asseblief 'n CSV lêer" }),
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
        <h1 className="text-3xl font-bold">Grootmaat Krediet Laai</h1>
        <p className="text-muted-foreground">
          Laai krediet vir meerdere gebruikers deur 'n CSV lêer te gebruik
        </p>
      </div>

      {loadSuccess && (
        <Alert className="mb-6 bg-green-50 border-green-500">
          <Check className="h-4 w-4 text-green-500" />
          <AlertTitle className="text-green-800">Sukses</AlertTitle>
          <div className="text-green-700">
            <p>Krediet is suksesvol gelaai:</p>
            <ul className="list-disc list-inside mt-2">
              <li>Suksesvolle transaksies: {result?.successfulLoads || 0}</li>
              <li>Mislukte transaksies: {result?.failedLoads || 0}</li>
              {result?.totalAmountLoaded && <li>Totale bedrag gelaai: R{result?.totalAmountLoaded.toFixed(2)}</li>}
            </ul>
          </div>
        </Alert>
      )}

      {bulkLoadMutation.isError && (
        <Alert className="mb-6 bg-red-50 border-red-500">
          <AlertTriangle className="h-4 w-4 text-red-500" />
          <AlertTitle className="text-red-800">Fout</AlertTitle>
          <div className="text-red-700">
            Daar was 'n probleem met die laai van krediet. Maak seker dat jou CSV lêer korrek geformateer is.
          </div>
          Mislukte transaksies: {result?.failedLoads || 0}
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
            <CardTitle>Laai CSV Lêer</CardTitle>
            <CardDescription>
              Laai 'n CSV lêer met gebruiker inligting en bedrae
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
                      <FormLabel>CSV Lêer</FormLabel>
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
                    'Besig om te laai...'
                  ) : (
                    <div className="flex items-center gap-2">
                      <Upload className="h-4 w-4" />
                      Laai CSV
                    </div>
                  )}
                </Button>
              </form>
            </Form>
          </CardContent>
        </Card>

        <Card className='bg-background'>
          <CardHeader>
            <CardTitle>CSV Formaat</CardTitle>
            <CardDescription>
              Hoe om jou CSV lêer voor te berei
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <p>
              Die CSV lêer moet in die volgende formaat wees:
            </p>
            
            <div className="bg-muted p-3 rounded-md font-mono text-sm overflow-x-auto">
              email,credential,amount,description<br />
              gebruiker1@example.com,,100.00,"Maandelikse Krediet"<br />
              ,202413312,50.00,"Ekstra Krediet"<br />
              ...
            </div>
            
            <div className="space-y-2">
              <p className="font-medium">Belangrike notas:</p>
              <ul className="list-disc list-inside text-sm space-y-1">
                <li>Die lêer moet 'n opskrif ry bevat soos bo getoon</li>
                <li>Elke ry moet 'n geldige e-posadres of student/personeelnommer, bedrag, en beskrywing bevat</li>
                <li>Bedrae moet numeries wees (gebruik punte vir desimale, nie kommas nie)</li>
                <li>Beskrywings met kommas moet in aanhalingstekens wees</li>
                <li>Die maksimum lêergrootte is 5MB</li>
              </ul>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
