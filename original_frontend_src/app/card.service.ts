import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

import { Card, Bioinfo, Artwork, Category } from './interface';
import { CARDS } from './mock-data';

@Injectable({
  providedIn: 'root'
})
export class CardService {

  private getCardUrl = "/search/"
  private getBioUrl = "/select/"
  private getArtworkUrl = "/endpoints/"
  private getGenreUrl = "/genes/"

  name: string = '';
  cards?: Card[];

  constructor(
	private http: HttpClient
  ) { }

  getCards(name: string): Observable<Card[]> {
    var url = this.getCardUrl + name;
	return this.http.get<Card[]>(url);
  }

  getBios(id: string): Observable<Bioinfo> {
    var url = this.getBioUrl + id;
	console.log(url);
	return this.http.get<Bioinfo>(url);
  }

  getArtworks(id: string): Observable<Artwork[]> {
    var url = this.getArtworkUrl + id;
	console.log(url);
	return this.http.get<Artwork[]>(url);
  }

  getCategories(id: string): Observable<Category[]> {
    var url = this.getGenreUrl + id;
	console.log(url);
	return this.http.get<Category[]>(url);
  }

  private handleError<T>(operation='operation', result?: T) {
	return (error: any): Observable<T> => {
		console.error(error);
		return of(result as T);
	}
  }
}
