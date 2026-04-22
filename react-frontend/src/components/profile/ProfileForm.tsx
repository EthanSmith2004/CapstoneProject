import { useForm } from '@tanstack/react-form'
import { UserPlus } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'

import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { Label } from '../ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card'
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from '../ui/select'
import { Checkbox } from '../ui/checkbox'
import { useAuth } from '../../contexts/AuthContext'
import { type CreateUserProfileRequest, ListApi, type SelectDTO, type UserProfileDTO } from '@/api'

export function ProfileForm({submit, isLoading, initialData}: {submit: (data: CreateUserProfileRequest) => void, isLoading?: boolean, initialData?: UserProfileDTO}) {
  const { getApiClient } = useAuth()
  const listAPI = getApiClient(ListApi)

  // Fetch dropdown data
  const { data: campusesResponse, isLoading: campusesLoading } = useQuery({
    queryKey: ['campuses'],
    queryFn: () => listAPI.getCampusNames()
  })

  const { data: residencesResponse, isLoading: residencesLoading } = useQuery({
    queryKey: ['residences'],
    queryFn: () => listAPI.getResidenceNames()
  })

  const { data: allergiesResponse, isLoading: allergiesLoading } = useQuery({
    queryKey: ['allergies'],
    queryFn: () => listAPI.getAllergyNames()
  })

  const campuses = campusesResponse?.data || (initialData?.campus ? [initialData?.campus] : [])
  const residences = residencesResponse?.data || (initialData?.residence ? [initialData?.residence] : [])
  const allergies = allergiesResponse?.data || (initialData?.allergies ? [initialData?.allergies] : [])

  const form = useForm({
    defaultValues: {
      credentialNumber: initialData?.credentialNumber || '',
      campusId: initialData?.campus?.id || 0,
      residenceId: initialData?.residence?.id || undefined,
      allergyIds: initialData?.allergies?.map((v) => v.id ?? 0) || []
    },
    onSubmit: async ({ value }) => {
      // Basic validation
      if (!value.credentialNumber || value.credentialNumber.trim() === '') {
        throw new Error('Student/Personeel nommer word versoek')
      }
      
      if (!value.campusId || value.campusId === 0) {
        throw new Error('Kampus is verpligtend')
      }
      
      try {
        const profileData: CreateUserProfileRequest = {
          credentialNumber: value.credentialNumber,
          campusId: value.campusId,
          residenceId: value.residenceId,
          allergyIds: value.allergyIds
        }
        submit(profileData)
      } catch (error) {
        // Error is already handled in the mutation onError
      }
    }
  })

  return (
    <div className="min-h-screen 
                    flex items-center 
                    justify-center
                    bg-gradient-to-bl
                    py-12 
                    px-4 
                    sm:px-6 
                    lg:px-8
                    spys-body"
    >
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1">
          <CardTitle className="text-2xl 
                                font-bold 
                                text-center"
          >
            Skep Profiel
          </CardTitle>
          <CardDescription className="text-center">
            Voer jou profiel inligting in om te begin
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form
            onSubmit={(e) => {
              e.preventDefault()
              e.stopPropagation()
              void form.handleSubmit()
            }}
            className="space-y-4"
          >
            <div className="space-y-2">
              <form.Field
                name="credentialNumber"
                validators={{
                  onChange: ({ value }) => {
                    if (!value || value.trim() === '') {
                      return 'Student/Personeel nommer word versoek'
                    }
                    return undefined
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="credentialNumber">Student/Personeel Nommer</Label>
                    <Input
                      id="credentialNumber"
                      name={field.name}
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(e) => field.handleChange(e.target.value)}
                      placeholder="Voer jou student of personeel nommer in"
                      autoComplete="off"
                    />
                    {field.state.meta.errors.length > 0 && (
                      <p className="text-sm text-red-600 mt-1">
                        {field.state.meta.errors[0]}
                      </p>
                    )}
                  </div>
                )}
              </form.Field>
            </div>

            <div className="space-y-2">
              <form.Field
                name="campusId"
                validators={{
                  onChange: ({ value }) => {
                    if (!value || value === 0) {
                      return 'Kampus is verpligtend'
                    }
                    return undefined
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="campusId">Kampus</Label>
                    <Select
                      value={field.state.value?.toString() || "0"}
                      onValueChange={(value) => field.handleChange(parseInt(value))}
                    >
                      <SelectTrigger id="campusId">
                        <SelectValue placeholder="Kies jou kampus" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="0" disabled>--Kies--</SelectItem>
                        {campusesLoading ? (
                          <SelectItem value="-1" disabled>Laai...</SelectItem>
                        ) : (
                          campuses.map((campus: SelectDTO) => (
                            <SelectItem key={campus.id} value={campus.id?.toString() || ""}>
                              {campus.name}
                            </SelectItem>
                          ))
                        )}
                      </SelectContent>
                    </Select>
                    {field.state.meta.errors.length > 0 && (
                      <p className="text-sm text-red-600 mt-1">
                        {field.state.meta.errors[0]}
                      </p>
                    )}
                  </div>
                )}
              </form.Field>
            </div>

            <div className="space-y-2">
              <form.Field
                name="residenceId"
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="residenceId">Koshuis (Opsioneel)</Label>
                    <Select
                      value={field.state.value?.toString() || "0"}
                      onValueChange={(value) => field.handleChange(value ? parseInt(value) : undefined)}
                    >
                      <SelectTrigger id="residenceId">
                        <SelectValue placeholder="Kies jou koshuis (indien van toepassing)" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="0">Geen</SelectItem>
                        {residencesLoading ? (
                          <SelectItem value="-1" disabled>Laai...</SelectItem>
                        ) : (
                          residences.map((residence: SelectDTO) => (
                            <SelectItem key={residence.id} value={residence.id?.toString() || ""}>
                              {residence.name}
                            </SelectItem>
                          ))
                        )}
                      </SelectContent>
                    </Select>
                  </div>
                )}
              </form.Field>
            </div>

            <div className="space-y-2">
              <form.Field
                name="allergyIds"
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="allergyIds">Allergieë (Opsioneel)</Label>
                    <div className="space-y-2 max-h-40 overflow-y-auto border rounded p-3">
                      {allergiesLoading ? (
                        <p className="text-sm text-gray-500">Laai allergieë...</p>
                      ) : allergies.length > 0 ? (
                        allergies.map((allergy: SelectDTO) => (
                          <div key={allergy.id} className="flex items-center space-x-2">
                            <Checkbox
                              id={`allergy-${allergy.id}`}
                              checked={field.state.value.includes(allergy.id || 0)}
                              onCheckedChange={(checked) => {
                                const allergyId = allergy.id || 0
                                if (checked) {
                                  field.handleChange([...field.state.value, allergyId])
                                } else {
                                  field.handleChange(field.state.value.filter(id => id !== allergyId))
                                }
                              }}
                            />
                            <Label 
                              htmlFor={`allergy-${allergy.id}`}
                              className="text-sm font-normal cursor-pointer"
                            >
                              {allergy.name}
                            </Label>
                          </div>
                        ))
                      ) : (
                        <p className="text-sm text-gray-500">Geen allergieë beskikbaar nie</p>
                      )}
                    </div>
                  </div>
                )}
              </form.Field>
            </div>

            <form.Subscribe
              selector={(state) => [state.canSubmit, state.isSubmitting]}
            >
              {([canSubmit, isSubmitting]) => (
                <Button
                  type="submit"
                  className="w-full"
                  disabled={!canSubmit || isSubmitting || isLoading}
                >
                  {(isSubmitting || isLoading) ? (
                    <>
                      <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-gray-300 border-t-white" />
                      Profiel word geskep...
                    </>
                  ) : (
                    <>
                      <UserPlus className="mr-2 h-4 w-4" />
                      Skep Profiel
                    </>
                  )}
                </Button>
              )}
            </form.Subscribe>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}