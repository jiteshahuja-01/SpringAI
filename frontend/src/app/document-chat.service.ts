import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class DocumentChatService {
  
  constructor(private http: HttpClient) {}

  uploadDocument(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(environment.uploadUrl, formData);
  }

  askQuestion(question: string): Observable<any> {
    return this.http.post<any>(environment.chatUrl, { question });
  }
}
