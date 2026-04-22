import { useForm } from '@tanstack/react-form'
import { z } from 'zod'
import { useNavigate } from '@tanstack/react-router'
import { UserPlus, Eye, EyeOff } from 'lucide-react'
import { useState } from 'react'

import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { Label } from '../ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card'
import { useAuth } from '../../contexts/AuthContext'

const registerSchema = z.object({
  firstName: z
    .string()
    .min(1, 'Naam word versoek'),
  lastName: z
    .string()
    .min(1, 'Van word versoek'),
  email: z
    .string()
    .min(1, 'E-pos word versoek')
    .email('E-pos moet geldig wees'),
  password: z
    .string()
    .min(6, 'Wagwoord moet ten minste 6 karakters bevat'),
  confirmPassword: z
    .string()
    .min(1, 'Bevestig asseblief jou wagwoord')
}).refine((data) => data.password === data.confirmPassword, {
  message: "Wagwoorde stem nie ooreen nie",
  path: ["confirmPassword"]
})

export function RegisterForm() {
  const { register, isLoading } = useAuth()
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const navigate = useNavigate()

  const form = useForm({
    defaultValues: {
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      confirmPassword: ''
    },
    onSubmit: async ({ value }) => {
      const validation = registerSchema.safeParse(value)
      if (!validation.success) {
        throw new Error('Validasie het misluk')
      }
      
      try {
        const { confirmPassword, ...registerData } = validation.data
        await register(registerData)
        navigate({ to: '/' })
      } catch (error) {
        // Error is already handled in the auth context with toast
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
            Registrasie
          </CardTitle>
          <CardDescription className="text-center">
            Voer jou inligting in om 'n profiel te skep
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
                name="firstName"
                validators={{
                  onChange: ({ value }) => {
                    const result = z
                      .string()
                      .min(1, 'Naam word versoek')
                      .safeParse(value)
                    return result.success ? undefined : result.error.issues[0]?.message
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="firstName">Naam</Label>
                    <Input
                      id="firstName"
                      name={field.name}
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(e) => field.handleChange(e.target.value)}
                      placeholder="Voer jou naam in"
                      autoComplete="name"
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
                name="lastName"
                validators={{
                  onChange: ({ value }) => {
                    const result = z
                      .string()
                      .min(1, 'Van word versoek')
                      .safeParse(value)
                    return result.success ? undefined : result.error.issues[0]?.message
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="lastName">Van</Label>
                    <Input
                      id="lastName"
                      name={field.name}
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(e) => field.handleChange(e.target.value)}
                      placeholder="Voer jou van in"
                      autoComplete="surname"
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
                name="email"
                validators={{
                  onChange: ({ value }) => {
                    const result = z
                      .string()
                      .min(1, 'E-pos word versoek')
                      .email('E-pos moet geldig wees')
                      .safeParse(value)
                    return result.success ? undefined : result.error.issues[0]?.message
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="email">E-pos</Label>
                    <Input
                      id="email"
                      name={field.name}
                      type="email"
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(e) => field.handleChange(e.target.value)}
                      placeholder="Voer jou e-pos in"
                      autoComplete="email"
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
                name="password"
                validators={{
                  onChange: ({ value }) => {
                    const result = z
                      .string()
                      .min(6, 'Wagwoord moet ten minste 6 karakters bevat')
                      .safeParse(value)
                    return result.success ? undefined : result.error.issues[0]?.message
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="password">Wagwoord</Label>
                    <div className="relative">
                      <Input
                        id="password"
                        name={field.name}
                        type={showPassword ? 'text' : 'password'}
                        value={field.state.value}
                        onBlur={field.handleBlur}
                        onChange={(e) => field.handleChange(e.target.value)}
                        placeholder="Skep jou wagwoord"
                        autoComplete="new-password"
                        className="pr-10"
                      />
                      <button
                        type="button"
                        className="absolute inset-y-0 right-0 pr-3 flex items-center"
                        onClick={() => setShowPassword(!showPassword)}
                      >
                        {showPassword ? (
                          <EyeOff className="h-4 w-4 text-gray-400" />
                        ) : (
                          <Eye className="h-4 w-4 text-gray-400" />
                        )}
                      </button>
                    </div>
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
                name="confirmPassword"
                validators={{
                  onChange: ({ value }) => {
                    const password = form.getFieldValue('password')
                    if (value !== password) {
                      return "Wagwoorde stem nie ooreen nie"
                    }
                    return undefined
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="confirmPassword">Bevestig Wagwoord</Label>
                    <div className="relative">
                      <Input
                        id="confirmPassword"
                        name={field.name}
                        type={showConfirmPassword ? 'text' : 'password'}
                        value={field.state.value}
                        onBlur={field.handleBlur}
                        onChange={(e) => field.handleChange(e.target.value)}
                        placeholder="Bevestig jou wagwoord"
                        autoComplete="new-password"
                        className="pr-10"
                      />
                      <button
                        type="button"
                        className="absolute 
                                  inset-y-0 
                                  right-0 
                                  pr-3 
                                  flex 
                                  items-center"
                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                      >
                        {showConfirmPassword ? (
                          <EyeOff className="h-4 w-4 text-gray-400" />
                        ) : (
                          <Eye className="h-4 w-4 text-gray-400" />
                        )}
                      </button>
                    </div>
                    {field.state.meta.errors.length > 0 && (
                      <p className="text-sm text-red-600 mt-1">
                        {field.state.meta.errors[0]}
                      </p>
                    )}
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
            <div className="text-center text-sm">
              <span className="text-gray-600">Het klaar 'n profiel? </span>
              <button
                type="button"
                onClick={() => window.location.href = '/login'}
                className="font-medium 
                          text-blue-600
                          hover:text-blue-500"
              >
                Teken in
              </button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
