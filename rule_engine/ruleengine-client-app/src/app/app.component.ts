import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Rule Engine Application';
  static API_URL="http://localhost:8080";
  //static API_URL="/ruleengine"; 
}
