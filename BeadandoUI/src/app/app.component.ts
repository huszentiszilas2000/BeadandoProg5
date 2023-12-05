import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import {OAuthService} from "angular-oauth2-oidc";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  NgForm,
  ReactiveFormsModule,
  Validators
} from "@angular/forms";
import {ModalDismissReasons, NgbModal, NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {delay} from "rxjs";

export class User {
  constructor(
    public id: string,
    public username: string,
    public email: string,
    public firstName: string,
    public lastName: string
  ) {
  }
}


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, FormsModule, NgbModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  users: User[];
  closeResult: string;
  deleteId: string;
  title = 'BeadandoUI';
  constructor(private oauthService: OAuthService, private httpClient : HttpClient, private modelService: NgbModal, private formBuilder: FormBuilder) {

  }
  ngOnInit(): void {
    this.getUsers();
  }
  getUsers() {
    this.httpClient.get<any>('http://localhost:8080/api/user/users',
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }}).pipe(delay(400)).subscribe(
      response => {
        this.users = response;
      }
    );
  }
  logout()
  {
    this.oauthService.logOut();
  }

  onSubmit(f: NgForm) {
    console.log("pressed");
    const url = 'http://localhost:8080/api/user/addUser';
    this.httpClient.post(url, f.value,
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }})
      .subscribe((result) => {
        this.ngOnInit();
      });
    this.modelService.dismissAll();
  }

  open(content) {
    this.modelService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  openDelete(targetModal, user: User) {
    this.deleteId = user.id;
    this.modelService.open(targetModal, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  onDelete()
  {
    const url = 'http://localhost:8080/api/user/' + this.deleteId;
    this.httpClient.delete(url,
      {headers: {
        'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
      }})
      .subscribe((results) => {
        this.ngOnInit();
      });
    this.modelService.dismissAll();
    this.ngOnInit();
  }
  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  protected readonly OAuthService = OAuthService;
}
