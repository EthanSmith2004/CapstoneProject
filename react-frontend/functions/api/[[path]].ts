interface FunctionContext {
  request: Request
  env: { BACKEND_URL: string }
}

// _redirects' 200-status proxying drops POST bodies when forwarding to an
// external origin, which breaks login/register. A Pages Function forwards
// method/headers/body correctly instead.
export async function onRequest(context: FunctionContext): Promise<Response> {
  const url = new URL(context.request.url)
  const target = `${context.env.BACKEND_URL}${url.pathname}${url.search}`
  return fetch(new Request(target, context.request))
}
