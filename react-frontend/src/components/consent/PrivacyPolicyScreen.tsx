import { AppBar } from '@/components/ui/appbar'

export function PrivacyPolicyScreen() {
  return (
    <div className="min-h-screen bg-white text-gray-800">
      <AppBar title="Privaatheidsbeleid" balance={0}  />

      <div className="p-6 pt-24 space-y-6 text-sm leading-relaxed">
        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">1. Versameling van Inligting</h2>
          <p>
            Ons versamel slegs die minimum persoonlike inligting wat benodig word om u aan te meld en u kosbestellings te verwerk. Dit sluit in u naam, studentenommer, en gekose maaltye.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">2. Gebruik van Inligting</h2>
          <p>
            U data word gebruik om u bestellings te verwerk, u rekeningbalans te bereken en aflewerings te organiseer. Geen inligting word aan derde partye verkoop of gedeel nie.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">3. Toestemming</h2>
          <p>
            Deur die “Ek stem saam” knoppie op die vorige skerm te klik, gee u uitdruklike toestemming dat ons u data mag verwerk soos in hierdie beleid uiteengesit.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">4. Beveiliging van Inligting</h2>
          <p>
            Alle data word beveilig gestoor in ’n databasis wat slegs toeganklik is vir gemagtigde personeel. Ons gebruik industrie-standaard praktyke vir databeskerming.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">5. U Regte</h2>
          <p>
            U het die reg om toegang tot u data aan te vra, dit reg te stel, of die verwerking daarvan te laat beëindig. U kan ook enige tyd u toestemming terugtrek.
          </p>
        </section>

        <section>
          <h2 className="text-lg font-bold text-orange-600 mb-2">6. Kontakbesonderhede</h2>
          <p>
            Enige navrae rakende hierdie beleid kan gestuur word na <span className="text-blue-600">admin@koshuiskos.co.za</span>.
          </p>
        </section>
      </div>
    </div>
  )
}
