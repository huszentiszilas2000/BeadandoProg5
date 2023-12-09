import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, NgForm, ReactiveFormsModule} from "@angular/forms";
import {NgForOf} from "@angular/common";
import {delay} from "rxjs";
import {ModalDismissReasons, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {HttpClient} from "@angular/common/http";
import {OAuthService} from "angular-oauth2-oidc";
import Swal from "sweetalert2";

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
  selector: 'app-usermanagement',
  standalone: true,
    imports: [
        FormsModule,
        NgForOf,
        ReactiveFormsModule
    ],
  templateUrl: './usermanagement.component.html',
  styleUrl: './usermanagement.component.css'
})

export class UsermanagementComponent implements OnInit  {

  users: User[];
  closeResult: string;
  userId: string;
  editForm: FormGroup;

  constructor(private oauthService: OAuthService, private httpClient : HttpClient, private modelService: NgbModal, private formBuilder: FormBuilder) {

  }

  ngOnInit(): void {
    this.getUsers();
    this.editForm = this.formBuilder.group({
      id:[''],
      username:[''],
      firstName:[''],
      lastName:[''],
      email:['']
    });
  }

  onSave()
  {
    const url = 'http://localhost:8080/api/user/update/' + this.editForm.value.id;
    this.httpClient.put(url, this.editForm.value,
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }})
      .subscribe((response:any) => {
        Swal.fire( 'Success', response.message, 'success');
      });
    this.ngOnInit();
    this.modelService.dismissAll();
  }

  openEdit(targetModal, user: User)
  {
    this.modelService.open(targetModal, {
      centered: true,
      backdrop: 'static',
      size:'lg'
    });
    this.editForm.patchValue(
      {
        id: user.id,
        username: user.username,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email
      }
    )
  }

  getUsers() {
    const url = 'http://localhost:8080/api/user/users'
    this.httpClient.get<any>(url,
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }}).pipe(delay(400)).subscribe(
      response => {
        this.users = response;
      }
    );
  }

  onSubmit(f: NgForm) {
    const url = 'http://localhost:8080/api/user/addUser';
    this.httpClient.post(url, f.value,
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }})
      .subscribe((response:any) => {
        Swal.fire( 'Success', response.message, 'success');
      });
    this.modelService.dismissAll();
    this.ngOnInit();
  }

  open(content) {
    this.modelService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  openWithUserID(targetModal, user: User) {
    this.userId = user.id;
    this.modelService.open(targetModal, {
      backdrop: 'static'
    });
  }

  onReset()
  {
    const url = 'http://localhost:8080/api/user/reset-password/' + this.userId;
    this.httpClient.get<any>(url,
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }})
      .subscribe((response:any) => {
        Swal.fire( 'Success', response.message, 'success');
      });
    this.modelService.dismissAll();
    this.ngOnInit();
  }

  onDelete()
  {
    const url = 'http://localhost:8080/api/user/' + this.userId;
    this.httpClient.delete(url,
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }})
      .subscribe((response:any) => {
          Swal.fire( 'Success', response.message, 'success');
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

}
