import { useForm } from '@tanstack/react-form'
import { z } from 'zod'
import { useNavigate } from '@tanstack/react-router'
import { LogIn, Eye, EyeOff } from 'lucide-react'
import { useState } from 'react'

import { Button } from '../ui/button'
import { Input } from '../ui/input'
import { Label } from '../ui/label'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card'
import { useAuth } from '../../contexts/AuthContext'

const loginSchema = z.object({
  email: z
    .string()
    .min(1, 'E-pos word versoek')
    .email('E-pos moet geldig wees'),
  password: z
    .string()
    .min(1, 'Wagwoord word versoek')
})

export function LoginForm() {
  const { login, isLoading } = useAuth()
  const [showPassword, setShowPassword] = useState(false)
  const navigate = useNavigate()

  const form = useForm({
    defaultValues: {
      email: '',
      password: ''
    },
    onSubmit: async ({ value }) => {
      const validation = loginSchema.safeParse(value)
      if (!validation.success) {
        throw new Error('Validasie het misluk')
      }
      
      try {
        await login(validation.data)
        navigate({ to: '/' })
      } catch (error) {
        // Error is already handled in the auth context with toast
      }
    }
  })

  return (
    <div className="min-h-screen 
                    flex 
                    items-center 
                    justify-center 
                    py-12 
                    px-4 
                    sm:px-6 
                    lg:px-8
                    spys-body"
    >
      <div className='w-full max-w-md spys-greyish-orange'>
        <Card className="w-full
                      max-w-md"
        >
          <CardHeader className="space-y-1">
            <CardTitle className="text-2xl
                                  font-bold 
                                  text-center"
            >
              Inteken
            </CardTitle>
            <CardDescription className="text-center">
              Voer asseblief die inligting in om aan te meld
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
                        .min(1, 'Wagwoord word versoek')
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
                          placeholder="Voer jou wagwoord in"
                          autoComplete="current-password"
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
                        Besig om in te teken..
                      </>
                    ) : (
                      <>
                        <LogIn className="mr-2 h-4 w-4" />
                        Teken In
                      </>
                    )}
                  </Button>
                )}
              </form.Subscribe>            
              <div className="text-center text-sm">
                <span className="text-gray-600">Het nie 'n profiel  nie? </span>
                <button
                  type="button"
                  onClick={() => window.location.href = '/register'}
                  className="font-medium text-blue-600 hover:text-blue-500"
                >
                  Registreer
                </button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
      
    </div>
  )
}
