import { Routes } from '@angular/router';
import {UsermanagementComponent} from "./usermanagement/usermanagement.component";
import {FilemanagerComponent} from "./filemanager/filemanager.component";

export const routes: Routes = [
  {path: 'usermanagement', component: UsermanagementComponent},
  {path: 'filemanager', component:FilemanagerComponent}];
