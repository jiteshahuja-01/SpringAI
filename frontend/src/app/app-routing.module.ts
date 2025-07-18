import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DocumentChatComponent } from './document-chat/document-chat.component';

const routes: Routes = [
  {path:'upload-chat', component: DocumentChatComponent},
  { path: '**', redirectTo: 'upload-chat' },
  { path: '', redirectTo: 'upload-chat', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
