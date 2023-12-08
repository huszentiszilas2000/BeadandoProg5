import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import {OAuthService} from "angular-oauth2-oidc";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {
  FormsModule,
  ReactiveFormsModule
} from "@angular/forms";
import {ModalDismissReasons, NgbModal, NgbModule} from "@ng-bootstrap/ng-bootstrap";


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, FormsModule, NgbModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent{
  title = 'BeadandoUI';
  constructor(private oauthService: OAuthService) {

  }

  logout()
  {
    this.oauthService.logOut();
  }



  protected readonly OAuthService = OAuthService;
}
