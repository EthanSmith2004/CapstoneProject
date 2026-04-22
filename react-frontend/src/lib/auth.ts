import { Configuration } from '../api'

const defaultConfig = {
  basePath: '',
}

export const getAuthConfig = (token: string): Configuration => {
  return new Configuration({
    ...defaultConfig,
    accessToken: token || undefined,
  })
}

export const configureApiWithAuth = <T>(token: string, ApiClass: new (config?: Configuration) => T): T => {
  const config = getAuthConfig(token)
  return new ApiClass(config)
}

export const configureAnonApi = <T>(ApiClass: new (config?: Configuration) => T): T => {
  const conf = new Configuration({
    ...defaultConfig
  });
  return new ApiClass(conf);
}
