import React from 'react'
import QRCode from 'react-qr-code'

interface UserBarcodeDisplayProps {
  credentialNumber: string
  firstName?: string
  lastName?: string
}

export const UserBarcodeDisplay: React.FC<UserBarcodeDisplayProps> = ({
  credentialNumber,
  firstName,
  lastName,
}) => {
  return (
    <div className="flex flex-col items-center p-6 bg-white rounded-lg shadow-lg mb-6">
      <h3 className="text-lg font-semibold mb-2">Jou Aflewerings Ontvangs Kode</h3>
      {firstName && lastName && (
        <p className="text-sm text-gray-600 mb-4">
          {firstName} {lastName}
        </p>
      )}
      <div className="bg-white p-4 rounded border-2 border-gray-200">
        <QRCode
          value={credentialNumber}
          size={200}
          level="H"
          className="max-w-full h-auto"
        />
      </div>
      <p className="mt-4 text-sm text-gray-600 text-center">
        Wys hierdie kode aan die afleweringspersoon om jou items te ontvang.
      </p>
      <p className="text-xs text-gray-500 mt-2 font-mono bg-gray-100 px-3 py-1 rounded">
        {credentialNumber}
      </p>
    </div>
  )
}
