import { useState } from 'react'
import { AppBar } from '@/components/ui/appbar'
import { SuccessOverlay } from '@/components/ui/successOverlay'

export function DataConsentScreen() {
  const [consentGiven, setConsentGiven] = useState(false)
  const [showError, setShowError] = useState(false)
  const [showSuccess, setShowSuccess] = useState(false)

  const handleConsent = () => {
    if (!consentGiven) {
      setShowError(true)
      setTimeout(() => setShowError(false), 3000)
      return
    }

    localStorage.setItem('dataConsentGiven', 'true')
    setShowSuccess(true)
  }

  return (
    <div className="min-h-screen bg-white pb-32 relative">
      <AppBar title="Data Usage" balance={1000} />

      <div className="p-6 pt-24">
        <div className="border border-gray-300 rounded-xl p-4 bg-gray-50 shadow-sm">
          <p className="text-lg text-gray-800">
            We collect certain personal information to improve your experience. By continuing, you agree to the processing of your data according to our privacy policy.
          </p>
        </div>

        <div className="mt-6 flex items-start gap-3">
          <input
            id="consent"
            type="checkbox"
            checked={consentGiven}
            onChange={(e) => setConsentGiven(e.target.checked)}
            className="accent-orange-600 w-5 h-5 border border-gray-400"
          />
          <label htmlFor="consent" className="text-gray-700 text-sm">
            I agree to the processing of my personal information <br />
            <a href="/policy" className="text-blue-600 underline text-xs">Read our privacy policy</a>
          </label>
        </div>

        {showError && (
          <div className="mt-4 text-red-600 bg-red-100 border border-red-400 rounded p-3">
            You must give consent to continue.
          </div>
        )}

        <button
          onClick={handleConsent}
          className="mt-8 w-full bg-orange-600 text-white py-3 rounded-full text-lg font-semibold shadow"
        >
          I Agree
        </button>
      </div>

      {showSuccess && (
        <SuccessOverlay
          message="Consent submitted!"
          redirectTo="/user/menu"
          delay={2500}
        />
      )}
    </div>
  )
}
