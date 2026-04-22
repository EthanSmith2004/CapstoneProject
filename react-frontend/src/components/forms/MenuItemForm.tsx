import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import type { AllergyEntity, MenuItemDTO } from '@/api';

const formSchema = z.object({
  name: z.string().min(1, 'Name is required'),
  description: z.string().optional(),
  price: z.number().positive('Price must be positive'),
  kcal: z.number().positive("Die kaloriee van 'n dis moet positief wees").optional(),
  imageHero: z.string().optional(),
  imageDetail: z.string().optional(),
  allergies: z.array(z.number()).optional(),
});

type FormValues = z.infer<typeof formSchema>;

interface MenuItemFormProps {
  onSubmit: (data: FormValues) => void;
  defaultValues?: MenuItemDTO;
  allergiesList?: AllergyEntity[];
}

export function MenuItemForm({ onSubmit, defaultValues, allergiesList }: MenuItemFormProps) {

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: defaultValues?.name ?? '',
      description: defaultValues?.description ?? '',
      price: defaultValues?.price ?? 0,
      kcal: defaultValues?.kcal ?? 0,
      imageHero: defaultValues?.imageHero ?? '',
      imageDetail: defaultValues?.imageDetail ?? '',
      allergies: defaultValues?.allergies?.map((value) =>
        allergiesList?.find((a) => a.allergy === value)?.id
      ) ?? [],
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        <div className="grid grid-cols-2 gap-4">
          <FormField
            control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Naam</FormLabel>
              <FormControl>
                <Input placeholder="Spaghetti" {...field} />
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
              <FormLabel>Beskrywing</FormLabel>
              <FormControl>
                <Input placeholder="A delicious plate of spaghetti." {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="price"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Prys</FormLabel>
              <FormControl>
                <Input type="number" {...field} onChange={e => field.onChange(parseFloat(e.target.value) || 0)} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="kcal"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Kcal</FormLabel>
              <FormControl>
                <Input type="number" {...field} onChange={e => field.onChange(parseFloat(e.target.value) || 0)} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="imageHero"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Spyskaart Prent</FormLabel>
              <FormControl>
                <Input placeholder="https://example.com/image.jpg" {...field} />
              </FormControl>
              {field.value && (
                <div className="mt-2">
                  <img 
                    src={field.value} 
                    className='w-full h-32 object-cover rounded-md border' 
                    alt="Spyskaart prent voorskou"
                  />
                </div>
              )}
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="imageDetail"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Detail Prent</FormLabel>
              <FormControl>
                <Input placeholder="https://example.com/image.jpg" {...field} />
              </FormControl>
              {field.value && (
                <div className="mt-2">
                  <img 
                    src={field.value} 
                    className='w-full h-32 object-cover rounded-md border' 
                    alt="Detail prent voorskou"
                  />
                </div>
              )}
              <FormMessage />
            </FormItem>
          )}
        />
        {allergiesList && (
          <FormField
            control={form.control}
            name="allergies"
            render={({ field }) => (
              <FormItem className="col-span-2">
                <FormLabel>Allergies</FormLabel>
                <FormControl>
                  <div className="grid grid-cols-2 gap-2 max-h-32 overflow-y-auto p-2 border rounded-md">
                    {allergiesList.map((allergy) => (
                      <label key={allergy.id} className="flex items-center gap-2 text-sm">
                        <input
                          type="checkbox"
                          value={allergy.id}
                          checked={field.value?.includes(allergy?.id ?? -1)}
                          onChange={(e) => {
                            const checked = e.target.checked;
                            console.log('Allergy checked:', allergy.id, field.value);
                            if (checked) {
                              field.onChange([...(field.value || []), allergy.id]);
                            } else {
                              field.onChange((field.value || []).filter((id: number) => id !== allergy.id));
                            }
                          }}
                          className="rounded"
                        />
                        <span className="truncate">{allergy.allergy}</span>
                      </label>
                    ))}
                  </div>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}
        </div>
        <div className="flex justify-end pt-4 border-t">
          <Button type="submit" className="min-w-[100px]">
            Submit
          </Button>
        </div>
      </form>
    </Form>
  );
}
