import React, { createContext, useContext, useEffect, useState, useRef, useCallback } from 'react'
import { AuthenticationApi } from '../api'
import type { AuthResponse, LoginRequest, RegisterRequest } from '../api/models'
import { Configuration } from '../api'
import { toast } from 'sonner'
import axios from 'axios'

interface User {
  email: string
  roles: string[]
}

interface AuthContextType {
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (credentials: LoginRequest) => Promise<void>
  register: (credentials: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  getApiClient: <T>(ApiClass: new (config?: Configuration) => T) => T
  isAdmin: boolean
  isUser: boolean
  isLoggingOut: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

const REFRESH_TOKEN_KEY = 'refreshToken'

// JWT utility
const parseJwt = (token: string): { exp?: number } => {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return {}
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [accessToken, setAccessToken] = useState<string | null>(null)
  const [refreshToken, setRefreshToken] = useState<string | null>(null)
  const [tokenExpiry, setTokenExpiry] = useState<number | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isLoggingOut, setIsLoggingOut] = useState(false)
  
  const refreshTimeoutRef = useRef<number | null>(null)
  const refreshPromiseRef = useRef<Promise<void> | null>(null)

  const isAuthenticated = !!user && !!accessToken
  const isAdmin = user?.roles.includes('ROLE_ADMIN') ?? false
  const isUser = user?.roles.includes('ROLE_USER') ?? false

  // Create anonymous API client
  const createAnonApi = useCallback(() => {
    const config = new Configuration({ basePath: '' })
    return new AuthenticationApi(config)
  }, [])

  // Get API client with current token
  const getApiClient = useCallback(<T,>(ApiClass: new (config?: Configuration) => T): T => {
    const config = new Configuration({
      basePath: '',
      accessToken: accessToken || undefined,
    })
    return new ApiClass(config)
  }, [accessToken])

  const clearAuthData = useCallback(() => {
    setUser(null)
    setAccessToken(null)
    setRefreshToken(null)
    setTokenExpiry(null)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    
    if (refreshTimeoutRef.current) {
      clearTimeout(refreshTimeoutRef.current)
      refreshTimeoutRef.current = null
    }
  }, [])

  const saveAuthData = useCallback((authResponse: AuthResponse) => {
    const userData: User = {
      email: authResponse.email || '',
      roles: authResponse.roles ? Array.from(authResponse.roles) : []
    }
    
    setUser(userData)
    setAccessToken(authResponse.accessToken || null)
    setRefreshToken(authResponse.refreshToken || null)
    
    if (authResponse.refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, authResponse.refreshToken)
    }
    
    if (authResponse.accessToken) {
      const { exp } = parseJwt(authResponse.accessToken)
      setTokenExpiry(exp ? exp * 1000 : null)
    } else {
      setTokenExpiry(null)
    }
  }, [])

  const performTokenRefresh = useCallback(async (): Promise<void> => {
    if (refreshPromiseRef.current) {
      await refreshPromiseRef.current
      return
    }

    const currentRefreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
    if (!currentRefreshToken) {
      clearAuthData()
      return
    }

    const refreshPromise = (async () => {
      try {
        const authApi = createAnonApi()
        const response = await authApi.refreshToken({ refreshToken: currentRefreshToken })
        saveAuthData(response.data)
        console.log('Token refreshed successfully')
      } catch (error) {
        console.error('Token refresh failed:', error)
        clearAuthData()
        toast.error('Session expired, please login again')
        // Redirect to login
        window.location.href = '/login'
        throw error
      }
    })()

    refreshPromiseRef.current = refreshPromise
    
    try {
      await refreshPromise
    } finally {
      refreshPromiseRef.current = null
    }
  }, [createAnonApi, saveAuthData, clearAuthData])

  // Auto-refresh timer
  useEffect(() => {
    if (!tokenExpiry || !accessToken) return

    const now = Date.now()
    const refreshTime = tokenExpiry - now - 2 * 60 * 1000 // 2 minutes before expiry
    
    if (refreshTimeoutRef.current) {
      clearTimeout(refreshTimeoutRef.current)
    }

    if (refreshTime > 0) {
      refreshTimeoutRef.current = window.setTimeout(() => {
        performTokenRefresh().catch(() => {
          // Error already handled in performTokenRefresh
        })
      }, refreshTime)
    }

    return () => {
      if (refreshTimeoutRef.current) {
        clearTimeout(refreshTimeoutRef.current)
      }
    }
  }, [tokenExpiry, accessToken, performTokenRefresh])

  // Initialize auth on mount
  useEffect(() => {
    const initAuth = async () => {
      const storedRefreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
      
      if (storedRefreshToken) {
        try {
          await performTokenRefresh()
        } catch {
          // Error already handled in performTokenRefresh
        }
      }
      
      setIsLoading(false)
    }

    initAuth()
  }, [performTokenRefresh])

  // Set up axios interceptor for 401 handling
  useEffect(() => {
    const interceptor = axios.interceptors.response.use(
      (response) => response,
      async (error) => {
        if (error.response?.status === 401 && accessToken) {
          try {
            await performTokenRefresh()
            // Retry the original request with new token
            if (error.config && accessToken) {
              error.config.headers.Authorization = `Bearer ${accessToken}`
              return axios.request(error.config)
            }
          } catch {
            // Token refresh failed, user will be logged out
          }
        }
        return Promise.reject(error)
      }
    )

    return () => {
      axios.interceptors.response.eject(interceptor)
    }
  }, [accessToken, performTokenRefresh])

  // Auth methods
  const login = async (credentials: LoginRequest) => {
    try {
      setIsLoading(true)
      const authApi = createAnonApi()
      const response = await authApi.authenticateUser(credentials)
      saveAuthData(response.data)
      toast.success('Login successful!')
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || 'Login failed'
      toast.error(errorMessage)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const register = async (credentials: RegisterRequest) => {
    try {
      setIsLoading(true)
      const authApi = createAnonApi()
      const response = await authApi.registerUser(credentials)
      saveAuthData(response.data)
      toast.success('Registration successful!')
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || 'Registration failed'
      toast.error(errorMessage)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const logout = async () => {
    try {
      if (refreshToken && !isLoggingOut) {
        const authApi = createAnonApi()
        setIsLoggingOut(true)
        await authApi.logout({ refreshToken })
      }
    } catch (error) {
      console.error('Logout API call failed:', error)
    } finally {
      clearAuthData()
      setIsLoggingOut(false)
    }
  }

  const value: AuthContextType = {
    user,
    isAuthenticated,
    isLoading,
    login,
    register,
    logout,
    getApiClient,
    isAdmin,
    isUser,
    isLoggingOut,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}