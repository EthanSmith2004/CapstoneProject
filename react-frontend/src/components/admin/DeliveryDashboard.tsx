import React, { useState, useRef, useEffect } from 'react'
import { useMutation, useQuery } from '@tanstack/react-query'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Loader2, Package, CheckCircle, TruckIcon, Camera, Keyboard } from 'lucide-react'
import { Scanner } from '@yudiel/react-qr-scanner'
import type { DeliveryAdminApi, UserDeliveryItemsResponse } from '@/api'

interface DeliveryDashboardProps {
  deliveryApi: DeliveryAdminApi
}

export const DeliveryDashboard: React.FC<DeliveryDashboardProps> = ({ deliveryApi }) => {
  const [userBarcode, setUserBarcode] = useState('')
  const [currentUser, setCurrentUser] = useState<UserDeliveryItemsResponse | null>(null)
  const [scanMode, setScanMode] = useState<'manual' | 'camera'>('manual')
  const [isCameraActive, setIsCameraActive] = useState(false)
  const inputRef = useRef<HTMLInputElement>(null)

  // Get statistics
  const { data: stats, refetch: refetchStats } = useQuery({
    queryKey: ['delivery-stats'],
    queryFn: async () => {
      const response = await deliveryApi.getDeliveryStatistics()
      return response.data
    },
    refetchInterval: 30000, // Refresh every 30 seconds
  })

  // Scan user mutation
  const scanUserMutation = useMutation({
    mutationFn: async (barcode: string) => {
      const response = await deliveryApi.scanUserBarcode({ barcode })
      return response.data
    },
    onSuccess: (data) => {
      setCurrentUser(data)
      toast.success(`Gebruiker gevind: ${data.firstName} ${data.lastName}`)
      playSuccessSound()
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Gebruiker nie gevind nie')
      playErrorSound()
      setUserBarcode('')
      inputRef.current?.focus()
    },
  })

  // Deliver item mutation
  const deliverItemMutation = useMutation({
    mutationFn: async ({ userBarcode, orderItemId }: { userBarcode: string; orderItemId: number }) => {
      const response = await deliveryApi.scanItemForDelivery({ userBarcode, orderItemId })
      return response.data
    },
    onSuccess: (data) => {
      toast.success(data.message)
      refetchStats()

      if (data.remainingItems === 0) {
        toast.success('Alle items afgelewer! ✓', {
          duration: 3000,
        })
        setCurrentUser(null)
        setUserBarcode('')
        inputRef.current?.focus()
      } else {
        // Refresh current user's items
        if (currentUser) {
          scanUserMutation.mutate(currentUser.credentialNumber ?? "")
        }
      }
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Kon nie item aflewer nie')
      playErrorSound()
    },
  })

  // Auto-focus input on mount and after operations
  useEffect(() => {
    if (scanMode === 'manual') {
      inputRef.current?.focus()
    }
  }, [currentUser, scanMode])

  // Toggle camera on/off when switching modes
  useEffect(() => {
    setIsCameraActive(scanMode === 'camera')
  }, [scanMode])

  const handleBarcodeSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (userBarcode.trim()) {
      scanUserMutation.mutate(userBarcode.trim())
    }
  }

  const handleQrScan = (result: string) => {
    if (result && !scanUserMutation.isPending) {
      setUserBarcode(result)
      scanUserMutation.mutate(result)
      setScanMode('manual') // Switch back to manual mode after successful scan
    }
  }

  const toggleScanMode = () => {
    setScanMode((prev) => (prev === 'manual' ? 'camera' : 'manual'))
    setUserBarcode('')
  }

  const handleItemDeliver = (itemId: number) => {
    if (currentUser) {
      deliverItemMutation.mutate({
        userBarcode: currentUser.credentialNumber ?? "",
        orderItemId: itemId,
      })
    }
  }

  const playSuccessSound = () => {
    try {
      const audio = new Audio('/sounds/success.mp3')
      audio.play().catch(() => {})
    } catch {
      // Fallback beep
      beep(800, 150)
    }
  }

  const playErrorSound = () => {
    try {
      const audio = new Audio('/sounds/error.mp3')
      audio.play().catch(() => {})
    } catch {
      // Fallback beep
      beep(200, 300)
    }
  }

  const beep = (frequency: number = 520, duration: number = 200) => {
    try {
      const audioContext = new AudioContext()
      const oscillator = audioContext.createOscillator()
      const gainNode = audioContext.createGain()

      oscillator.connect(gainNode)
      gainNode.connect(audioContext.destination)

      oscillator.frequency.value = frequency
      oscillator.type = 'sine'

      gainNode.gain.setValueAtTime(0.3, audioContext.currentTime)
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + duration / 1000)

      oscillator.start(audioContext.currentTime)
      oscillator.stop(audioContext.currentTime + duration / 1000)
    } catch {
      // Silent fail if audio context not available
    }
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6 flex items-center gap-2">
        <TruckIcon className="h-8 w-8" />
          Aflewering
      </h1>

      {/* Statistics */}
      {stats && (
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          <StatCard label="Totaal Vandag" value={stats.totalItemsToday ?? 0} color="blue" icon={Package} />
          <StatCard
            label="In Aflewering"
            value={stats.itemsInDelivery ?? 0}
            color="orange"
            icon={TruckIcon}
          />
          <StatCard
            label="Afgelewer"
            value={stats.itemsDelivered ?? 0}
            color="green"
            icon={CheckCircle}
          />
          <StatCard
            label="Voltooi"
            value={`${stats.completionPercentage?.toFixed(0) ?? 0}%`}
            color="purple"
          />
        </div>
      )}

      {/* Scanner Input */}
      <div className="bg-white rounded-lg shadow-lg p-6 mb-6 border-2 border-blue-200">
        <div className="flex items-center justify-between mb-3">
          <Label htmlFor="barcode-input" className="block text-base font-medium">
            Skandeer Gebruiker Kode
          </Label>
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={toggleScanMode}
            className="gap-2"
          >
            {scanMode === 'manual' ? (
              <>
                <Camera className="h-4 w-4" />
                Gebruik Kamera
              </>
            ) : (
              <>
                <Keyboard className="h-4 w-4" />
                Gebruik Sleutelbord
              </>
            )}
          </Button>
        </div>

        {scanMode === 'manual' ? (
          <form onSubmit={handleBarcodeSubmit}>
            <div className="flex gap-3">
              <Input
                id="barcode-input"
                ref={inputRef}
                type="text"
                value={userBarcode}
                onChange={(e) => setUserBarcode(e.target.value)}
                placeholder="Skandeer of tik barcode..."
                className="flex-1 text-lg h-12"
                disabled={scanUserMutation.isPending}
                autoFocus
                autoComplete="off"
              />
              <Button
                type="submit"
                disabled={scanUserMutation.isPending || !userBarcode.trim()}
                className="px-6 h-12"
                size="lg"
              >
                {scanUserMutation.isPending ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Skandeer...
                  </>
                ) : (
                  'Soek'
                )}
              </Button>
            </div>
            <p className="text-sm text-gray-500 mt-2">
              Skandeer die gebruiker se barcode om hul hangende items te sien
            </p>
          </form>
        ) : (
          <div className="space-y-3">
            <div className="relative aspect-video bg-black rounded-lg overflow-hidden">
              {isCameraActive && (
                <Scanner
                  onScan={(detectedCodes) => {
                    const code = detectedCodes[0]?.rawValue
                    if (code) {
                      handleQrScan(code)
                    }
                  }}
                  onError={(error: any) => {
                    console.error('QR Scanner Error:', error)
                    toast.error('Kamera fout: ' + (error?.message || 'Onbekende fout'))
                  }}
                  constraints={{
                    facingMode: 'environment', // Use back camera on mobile
                  }}
                  styles={{
                    container: {
                      width: '100%',
                      height: '100%',
                    },
                  }}
                />
              )}
            </div>
            <p className="text-sm text-gray-500 text-center">
              Posisioneer die QR kode binne die kamera se blikveld
            </p>
          </div>
        )}
      </div>

      {/* Current User Display */}
      {currentUser && (
        <div className="bg-white rounded-lg shadow-lg p-6 border-2 border-green-200">
          <div className="border-b pb-4 mb-4">
            <div className="flex items-start justify-between">
              <div>
                <h2 className="text-2xl font-bold text-green-700">
                  {currentUser.firstName} {currentUser.lastName}
                </h2>
                <p className="text-gray-600 mt-1">
                  {currentUser.residence && currentUser.campus && (
                    <>
                      {currentUser.residence} - {currentUser.campus}
                    </>
                  )}
                </p>
                <p className="text-sm text-gray-500 font-mono bg-gray-100 px-2 py-1 rounded mt-2 inline-block">
                  {currentUser.credentialNumber}
                </p>
              </div>
              <div className="bg-green-100 text-green-800 px-4 py-2 rounded-lg font-semibold">
                {currentUser.totalItems} {currentUser.totalItems === 1 ? 'item' : 'items'}
              </div>
            </div>
          </div>

          <h3 className="font-semibold mb-3 text-lg">Items om af te lewer:</h3>

          {currentUser.pendingItems?.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              <CheckCircle className="h-16 w-16 mx-auto mb-3 text-green-500" />
              <p>Geen hangende items nie</p>
            </div>
          ) : (
            <div className="space-y-3">
              {currentUser.pendingItems?.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 border border-gray-200"
                >
                  <div className="flex-1">
                    <p className="font-medium text-lg">{item.name}</p>
                    <p className="text-sm text-gray-600">
                      Hoeveelheid: {item.quantity} × R{item.price?.toFixed(2)} = R
                      {item.totalPrice?.toFixed(2)}
                    </p>
                    {item.description && (
                      <p className="text-xs text-gray-500 mt-1">{item.description}</p>
                    )}
                  </div>
                  <Button
                    onClick={() => handleItemDeliver(item?.id ?? 0)}
                    disabled={deliverItemMutation.isPending}
                    className="bg-green-500 hover:bg-green-600 text-white ml-4"
                    size="lg"
                  >
                    {deliverItemMutation.isPending ? (
                      <Loader2 className="h-5 w-5 animate-spin" />
                    ) : (
                      <>
                        <CheckCircle className="mr-2 h-5 w-5" />
                        Afgelewer
                      </>
                    )}
                  </Button>
                </div>
              ))}
            </div>
          )}

          <Button
            onClick={() => {
              setCurrentUser(null)
              setUserBarcode('')
              inputRef.current?.focus()
            }}
            variant="outline"
            className="mt-6 w-full"
            size="lg"
          >
            Kanselleer / Nuwe Skandering
          </Button>
        </div>
      )}

      {/* Empty state when no user scanned */}
      {!currentUser && !scanUserMutation.isPending && (
        <div className="text-center py-12 text-gray-400">
          <Package className="h-24 w-24 mx-auto mb-4 opacity-50" />
          <p className="text-lg">Skandeer 'n gebruiker se barcode om te begin</p>
        </div>
      )}
    </div>
  )
}

// Stat Card Component
interface StatCardProps {
  label: string
  value: string | number
  color: 'blue' | 'orange' | 'green' | 'purple'
  icon?: React.ElementType
}

const StatCard: React.FC<StatCardProps> = ({ label, value, color, icon: Icon }) => {
  const colorClasses = {
    blue: 'bg-blue-100 text-blue-800 border-blue-300',
    orange: 'bg-orange-100 text-orange-800 border-orange-300',
    green: 'bg-green-100 text-green-800 border-green-300',
    purple: 'bg-purple-100 text-purple-800 border-purple-300',
  }

  return (
    <div className={`rounded-lg p-4 border-2 ${colorClasses[color]}`}>
      <div className="flex items-center justify-between mb-2">
        <p className="text-sm opacity-80 font-medium">{label}</p>
        {Icon && <Icon className="h-5 w-5 opacity-60" />}
      </div>
      <p className="text-3xl font-bold">{value}</p>
    </div>
  )
}
