import { AppBar } from '@/components/ui/appbar'

export function PrivacyPolicyScreen() {
  return (
    <div className="min-h-screen bg-white text-gray-800">
      <AppBar title="Privacy Policy" balance={0} />

      <div className="p-6 pt-24 space-y-6 text-sm leading-relaxed">
        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">1. Information Collection</h2>
          <p>
            We only collect the minimum personal information needed to sign you in and process your meal orders. This includes your name, student number, and selected meals.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">2. Use of Information</h2>
          <p>
            Your data is used to process your orders, calculate your account balance, and organize deliveries. No information is sold or shared with third parties.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">3. Consent</h2>
          <p>
            By clicking the "I agree" button on the previous screen, you give explicit consent for us to process your data as outlined in this policy.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">4. Information Security</h2>
          <p>
            All data is stored securely in a database that is only accessible to authorized staff. We use industry-standard practices for data protection.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">5. Your Rights</h2>
          <p>
            You have the right to request access to your data, correct it, or ask for its processing to be stopped. You may also withdraw your consent at any time.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">6. Contact Details</h2>
          <p>
            Any questions about this policy can be sent to <span className="text-blue-600">admin@koshuiskos.co.za</span>.
          </p>
        </section>
      </div>
    </div>
  )
}
