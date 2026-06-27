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
    .min(1, 'First name is required'),
  lastName: z
    .string()
    .min(1, 'Last name is required'),
  email: z
    .string()
    .min(1, 'Email is required')
    .email('Email must be valid'),
  password: z
    .string()
    .min(6, 'Password must be at least 6 characters'),
  confirmPassword: z
    .string()
    .min(1, 'Please confirm your password')
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords do not match",
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
        throw new Error('Validation failed')
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
            Register
          </CardTitle>
          <CardDescription className="text-center">
            Enter your details to create an account
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
                      .min(1, 'First name is required')
                      .safeParse(value)
                    return result.success ? undefined : result.error.issues[0]?.message
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="firstName">First Name</Label>
                    <Input
                      id="firstName"
                      name={field.name}
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(e) => field.handleChange(e.target.value)}
                      placeholder="Enter your first name"
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
                      .min(1, 'Last name is required')
                      .safeParse(value)
                    return result.success ? undefined : result.error.issues[0]?.message
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="lastName">Last Name</Label>
                    <Input
                      id="lastName"
                      name={field.name}
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(e) => field.handleChange(e.target.value)}
                      placeholder="Enter your last name"
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
                      .min(1, 'Email is required')
                      .email('Email must be valid')
                      .safeParse(value)
                    return result.success ? undefined : result.error.issues[0]?.message
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      name={field.name}
                      type="email"
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(e) => field.handleChange(e.target.value)}
                      placeholder="Enter your email"
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
                      .min(6, 'Password must be at least 6 characters')
                      .safeParse(value)
                    return result.success ? undefined : result.error.issues[0]?.message
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="password">Password</Label>
                    <div className="relative">
                      <Input
                        id="password"
                        name={field.name}
                        type={showPassword ? 'text' : 'password'}
                        value={field.state.value}
                        onBlur={field.handleBlur}
                        onChange={(e) => field.handleChange(e.target.value)}
                        placeholder="Create your password"
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
                      return "Passwords do not match"
                    }
                    return undefined
                  }
                }}
              >
                {(field) => (
                  <div className="space-y-2">
                    <Label htmlFor="confirmPassword">Confirm Password</Label>
                    <div className="relative">
                      <Input
                        id="confirmPassword"
                        name={field.name}
                        type={showConfirmPassword ? 'text' : 'password'}
                        value={field.state.value}
                        onBlur={field.handleBlur}
                        onChange={(e) => field.handleChange(e.target.value)}
                        placeholder="Confirm your password"
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
                      Creating account...
                    </>
                  ) : (
                    <>
                      <UserPlus className="mr-2 h-4 w-4" />
                      Create Account
                    </>
                  )}
                </Button>
              )}
            </form.Subscribe>            
            <div className="text-center text-sm">
              <span className="text-gray-600">Already have an account? </span>
              <button
                type="button"
                onClick={() => window.location.href = '/login'}
                className="font-medium 
                          text-blue-600
                          hover:text-blue-500"
              >
                Sign in
              </button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
