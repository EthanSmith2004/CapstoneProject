import { AdminFinanceApi, type AdminLoadCreditRequest, type CompactUserDTO } from '@/api';
import { createFileRoute } from '@tanstack/react-router';
import { useAuth } from '@/contexts/AuthContext';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
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
import { Textarea } from '@/components/ui/textarea';
import { Alert, AlertTitle } from '@/components/ui/alert';
import { Check, Info, Search, AlertTriangle } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';

export const Route = createFileRoute('/admin/finance/load-individual')({
  component: RouteComponent,
})

const formSchema = z.object({
  amount: z.coerce.number().positive({ message: 'Amount must be positive' }),
  description: z.string().min(3, { message: 'Description must be at least 3 characters' }),
  query: z.string().optional(),
})

function RouteComponent() {
  const { getApiClient } = useAuth();
  const financeAPI = getApiClient(AdminFinanceApi);

  const queryClient = useQueryClient();
  
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedUser, setSelectedUser] = useState<CompactUserDTO | null>(null);  const [searchTimeout, setSearchTimeout] = useState<ReturnType<typeof setTimeout> | null>(null);
  const [loadSuccess, setLoadSuccess] = useState(false);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      amount: 0,
      description: '',
      query: '',
    },
  });

  const { data: searchResults, isFetching } = useQuery({
    queryKey: ['userSearch', searchQuery],
    queryFn: async () => {
      if (!searchQuery || searchQuery.length < 3) return { data: [] };
      return await financeAPI.findUsersSearch(searchQuery, 0, 10);
    },
    enabled: searchQuery.length >= 3,
    staleTime: 1000 * 60, 
  });

  const loadCreditMutation = useMutation({
    mutationFn: (data: AdminLoadCreditRequest) => financeAPI.loadUserCredit(data),
    onSuccess: () => {
      setLoadSuccess(true);
      form.reset({
        amount: 0,
        description: '',
        query: '',
      });
      setSelectedUser(null);
      
      queryClient.invalidateQueries({ queryKey: ['adminTransactions']});
      queryClient.invalidateQueries({ predicate: query => query.queryKey[0] === 'financialSummary' }); 

      // Hide success message after 3 seconds
      setTimeout(() => setLoadSuccess(false), 3000);
    },
  });
  const handleSearch = (value: string) => {
    if (searchTimeout) clearTimeout(searchTimeout);
    
    const timeout = setTimeout(() => {
      setSearchQuery(value);
    }, 500); // Debounce search input by 500ms
    
    setSearchTimeout(timeout);
  };

  const handleSelectUser = (user: CompactUserDTO) => {
    setSelectedUser(user);
    form.setValue('query', `${user.firstName} ${user.lastName} (${user.email})`);
  };

  const handleSubmit = (values: z.infer<typeof formSchema>) => {
    if (!selectedUser) return;
    
    loadCreditMutation.mutate({
      email: selectedUser.email,
      amount: values.amount,
      description: values.description
    });
  };

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Individual Credit Load</h1>
        <p className="text-muted-foreground">
          Add credit to an individual user's account
        </p>
      </div>      {loadSuccess && (
        <Alert className="mb-6 bg-green-50 border-green-500">
          <Check className="h-4 w-4 text-green-500" />
          <AlertTitle className="text-green-800">Success</AlertTitle>
          <div className="text-green-700">
            Credit loaded successfully to the user's account.
          </div>
        </Alert>
      )}

      {loadCreditMutation.isError && (
        <Alert className="mb-6 bg-red-50 border-red-500">
          <AlertTriangle className="h-4 w-4 text-red-500" />
          <AlertTitle className="text-red-800">Error</AlertTitle>
          <div className="text-red-700">
            There was a problem loading credit. Please try again.
          </div>
        </Alert>
      )}

      <Card className='bg-background'>
        <CardHeader>
          <CardTitle>Load Credit</CardTitle>
          <CardDescription>Search for a user and specify the amount to load</CardDescription>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
              <div className="space-y-4">
                <FormField
                  control={form.control}
                  name="query"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Search User</FormLabel>
                      <div className="relative">
                        <FormControl>
                          <Input 
                            placeholder="Search by name or email"
                            {...field} 
                            onChange={(e) => {
                              field.onChange(e);
                              if (!selectedUser) handleSearch(e.target.value);
                            }}
                            className="pr-10"
                          />
                        </FormControl>
                        <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                      </div>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                {!selectedUser && searchQuery.length >= 3 && (
                  <div className="border rounded-md max-h-60 overflow-y-auto">
                    {isFetching ? (
                      <div className="p-4 text-center">Searching...</div>
                    ) : searchResults?.data?.length === 0 ? (
                      <div className="p-4 text-center">No users found.</div>
                    ) : (
                      <ul>
                        {searchResults?.data.map((user) => (
                          <li 
                            key={user.id} 
                            className="p-2 hover:bg-muted cursor-pointer border-b last:border-0"
                            onClick={() => handleSelectUser(user)}
                          >
                            <div className="font-medium">{user.firstName} {user.lastName}</div>
                            <div className="text-sm text-muted-foreground">{user.email}</div>
                          </li>
                        ))}
                      </ul>
                    )}
                  </div>
                )}

                {selectedUser && (
                  <div className="p-4 border rounded-md bg-muted/30">
                    <div className="flex items-start gap-3">
                      <Info className="h-5 w-5 text-blue-500 mt-0.5" />
                      <div>
                        <div className="font-medium">Selected User</div>
                        <div>{selectedUser.firstName} {selectedUser.lastName}</div>
                        <div className="text-sm text-muted-foreground">{selectedUser.email}</div>
                      </div>
                    </div>
                  </div>
                )}

                <FormField
                  control={form.control}
                  name="amount"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Amount (R)</FormLabel>
                      <FormControl>
                        <Input type="number" step="0.01" placeholder="0.00" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="description"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Description</FormLabel>
                      <FormControl>
                        <Textarea placeholder="Provide a description for the transaction" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <Button 
                type="submit" 
                disabled={!selectedUser || form.formState.isSubmitting || loadCreditMutation.isPending}
              >
                {loadCreditMutation.isPending ? 'Loading...' : 'Load Credit'}
              </Button>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
}
