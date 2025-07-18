import { Component } from '@angular/core';
import { DocumentChatService } from '../document-chat.service';

@Component({
  selector: 'app-document-chat',
  templateUrl: './document-chat.component.html',
  styleUrls: ['./document-chat.component.css']
})
export class DocumentChatComponent {
  selectedFile: File | null = null;
  question: string = '';
  answer: string = '';
  uploading: boolean = false;
  chatting: boolean = false;

  constructor(private docService: DocumentChatService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files?.[0] || null;
  }

  uploadFile(): void {
    if (!this.selectedFile) return;

    this.uploading = true;
    this.docService.uploadDocument(this.selectedFile).subscribe(response => {
      alert('File uploaded successfully!');
      this.uploading = false;
    }, error => {
      alert('Error uploading file: ' + error.message);
      console.error(error);
      this.uploading = false;
    });
  }

  askQuestion(): void {
    if (!this.question.trim()) return;

    this.chatting = true;
    this.docService.askQuestion(this.question).subscribe({
      next: (res) => {
        this.answer = res.answer;
        this.chatting = false;
      },
      error: err => {
        console.error(err);
        this.answer = 'Error getting response.';
        this.chatting = false;
      }
    });
  }
}
