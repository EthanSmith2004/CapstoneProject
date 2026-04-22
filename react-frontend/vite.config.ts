import { defineConfig } from 'vite'
import viteReact from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { VitePWA } from 'vite-plugin-pwa'
import tanstackRouter from '@tanstack/router-plugin/vite'
import { resolve, dirname } from 'node:path'
import { fileURLToPath } from 'url'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    VitePWA({
      strategies: 'injectManifest',
      srcDir: 'public',
      filename: 'sw-push.js',
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico'],
      injectRegister: 'auto',
      manifest: {
        name: 'Spys Bestellings',
        short_name: 'Spys',
        start_url: '/',
        display: 'standalone',
        background_color: '#ffffff',
        theme_color: '#000000',
        orientation: 'portrait-primary',
        categories: ['food', 'shopping'],
        description: 'Order food from Spys with push notifications for updates',
        icons: [
          {
            src: 'logo192.png',
            sizes: '192x192',
            type: 'image/png',
            purpose: 'any maskable'
          },
          {
            src: 'logo512.png',
            sizes: '512x512',
            type: 'image/png',
            purpose: 'any maskable'
          },
        ],
      },
      injectManifest: {
        globPatterns: ['**/*.{js,css,html,ico,png,svg,woff2}']
      },
      devOptions: {
        enabled: true
      }
    }),
    tanstackRouter({ target: 'react', autoCodeSplitting: true }),
    viteReact(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      '@': resolve(dirname(fileURLToPath(import.meta.url)), './src')
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'https://spys-dev-backend.superuserdone.com/',
        changeOrigin: true,
        secure: true,
      },
    },
  },
})
