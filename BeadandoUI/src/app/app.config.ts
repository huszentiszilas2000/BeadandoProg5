import {APP_INITIALIZER, ApplicationConfig} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideHttpClient} from "@angular/common/http";
import {AuthConfig, OAuthEvent, OAuthService, provideOAuthClient} from "angular-oauth2-oidc";

export const authCodeConfig: AuthConfig =
{
  issuer:'http://localhost:8180/realms/beadando',
  tokenEndpoint: 'http://localhost:8180/realms/beadando/protocol/openid-connect/token',
  redirectUri:window.location.origin,
  clientId: 'beadando',
  responseType: 'code',
  scope: 'openid profile'
}

function initializeOAuth(oauthService: OAuthService): Promise<void>{
  return new Promise((resolve)=>{
    oauthService.configure(authCodeConfig);
    oauthService.setupAutomaticSilentRefresh();
    oauthService.loadDiscoveryDocumentAndLogin().then(() => resolve())
  })
}

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes),
  provideHttpClient(),
  provideOAuthClient(),
    {
      provide: APP_INITIALIZER,
      useFactory: (oauthService: OAuthService)=>{
        return () =>{
          initializeOAuth(oauthService);
        }
      },
      multi: true,
      deps:[OAuthService]
    }
  ]
};
