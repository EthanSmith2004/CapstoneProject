import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import type { UserEntity } from '@/api';
import { RolesEnum } from '@/api/models/user-entity';
import { Checkbox } from '@/components/ui/checkbox';

interface UserFormProps {
  onSubmit: (data: any) => void;
  defaultValues?: UserEntity;
}

export function UserForm({ onSubmit, defaultValues }: UserFormProps) {
  const isEditMode = !!defaultValues;

  // Dynamic schema: password required for new users, optional for editing
  const formSchema = z.object({
    firstName: z.string().min(1, 'First name is required'),
    lastName: z.string().min(1, 'Last name is required'),
    email: z.string().email('Invalid email address'),
    password: isEditMode 
      ? z.string().optional().or(z.literal(''))
      : z.string().min(6, 'Password must be at least 6 characters'),
    roles: z.array(z.nativeEnum(RolesEnum)).optional(),
    enabled: z.boolean().optional(),
    accountNonExpired: z.boolean().optional(),
    accountNonLocked: z.boolean().optional(),
    credentialsNonExpired: z.boolean().optional(),
  });

  type FormValues = z.infer<typeof formSchema>;

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      ...defaultValues,
      roles: defaultValues?.roles ? Array.from(defaultValues.roles) : [],
      enabled: defaultValues?.enabled ?? true,
      password: '',
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
        <div className="grid grid-cols-2 gap-8">
          <FormField
            control={form.control}
          name="firstName"
          render={({ field }) => (
            <FormItem>
              <FormLabel>First Name</FormLabel>
              <FormControl>
                <Input placeholder="John" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="lastName"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Last Name</FormLabel>
              <FormControl>
                <Input placeholder="Doe" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="email"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Email</FormLabel>
              <FormControl>
                <Input placeholder="john.doe@example.com" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="password"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Password {isEditMode && '(optional)'}</FormLabel>
              <FormControl>
                <Input 
                  type="password" 
                  placeholder={isEditMode ? 'Leave blank to keep the current password...' : 'Enter a password...'} 
                  {...field} 
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="roles"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Roles</FormLabel>
              <FormControl>
                <div className="flex flex-col gap-2">
                  {Object.values(RolesEnum).map((role) => (
                    <FormItem key={role} className="flex flex-row items-start space-y-0 rounded-md">
                      <FormControl>
                        <Checkbox checked={field.value?.includes(role)} onCheckedChange={(e) => {
                                  const checked = e as boolean;
                                  if (checked) {
                                    field.onChange([...(field.value || []), role]);
                                  } else {
                                    field.onChange((field.value || []).filter((r: RolesEnum) => r !== role));
                                  }
                                }} />
                      </FormControl>
                      <div className="space-y-1 leading-none">
                        <FormLabel>{role}</FormLabel>
                      </div>
                    </FormItem>
                  ))}
                </div>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="enabled"
          render={({ field }) => (
            <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md p-4">
              <FormControl>
                <Checkbox checked={field.value} onCheckedChange={field.onChange} />
              </FormControl>
              <div className="space-y-1 leading-none">
                <FormLabel>Active</FormLabel>
              </div>
            </FormItem>
          )}
        />
        </div>
        <Button type="submit">Save</Button>
      </form>
    </Form>
  );
}
