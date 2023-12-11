import {Component, OnInit} from '@angular/core';
import {NgForOf} from "@angular/common";
import {OAuthService} from "angular-oauth2-oidc";
import {HttpClient, HttpErrorResponse, HttpEvent} from "@angular/common/http";
import {ModalDismissReasons, NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {delay, Observable} from "rxjs";
import Swal from 'sweetalert2';
import * as FileSaver from 'file-saver';

export class FileName {
  constructor(
    public filename: string,
    public date_added: string,
  ) {
  }
}

@Component({
  selector: 'app-filemanager',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './filemanager.component.html',
  styleUrl: './filemanager.component.css'
})
export class FilemanagerComponent implements OnInit {
  files: FileName[];
  closeResult: string;
  public form: FormGroup;
  selectedFile: File;
  deleteFile: string;
  constructor(private fb: FormBuilder, private oauthService: OAuthService, private httpClient : HttpClient, private modelService: NgbModal, private formBuilder: FormBuilder) {

  }


  ngOnInit(): void {
    this.getFiles();
    this.form = this.fb.group({
      file: [null]
    })
  }

  openUpload(content) {
    this.modelService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  onFileSelected(event) {
    this.selectedFile = event.target.files[0];
    console.log(this.selectedFile);
  }

  download(file)
  {
    this.httpClient.get<Blob>('http://localhost:8080/api/file/download/' + file.filename,
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }, responseType: 'blob' as 'json'  }).subscribe(
      (response: any) => {
        let dataType = response.type;
        let binaryData: any = [];
        binaryData.push(response);
        FileSaver.saveAs(new Blob(binaryData, {type: dataType}), file.filename);
      },error => {
        Swal.fire( 'Error', error.message, 'error');}
    );
  }

  onSubmit() {
    const payload = new FormData();
    payload.append('file', this.selectedFile, this.selectedFile.name);
    this.httpClient
      .post(`http://localhost:8080/api/file/upload`,
        payload, {
          headers: {
            'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
          }
          }
      ).subscribe((response: any) => {
        Swal.fire( 'Success', response.message, 'success');},
  error => {
        Swal.fire( 'Error', error.message, 'error');
    });
    this.modelService.dismissAll();
  }

  openDelete(content, file)
  {
    this.deleteFile = file.filename;
    this.modelService.open(content, {
      backdrop: 'static',
      size: 'lg'
    });
  }

  onDelete()
  {
    this.httpClient.delete<any>('http://localhost:8080/api/file/delete/' + this.deleteFile,
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }}).pipe(delay(400)).subscribe(
      response => {
        Swal.fire( 'Success', response.message, 'success');
      },
      error =>{
        Swal.fire( 'Error', error.message, 'error');
      }
    );
    this.ngOnInit();
    this.modelService.dismissAll();
  }

  getFiles() {
    this.httpClient.get<any>('http://localhost:8080/api/file/myfiles',
      {headers: {
          'Authorization' : `Bearer ${this.oauthService.getAccessToken()}`
        }}).pipe(delay(400)).subscribe(
      response => {
        this.files = response;
      },
      error =>{
        Swal.fire( 'Error', error.message, 'error');
      }
    );
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
