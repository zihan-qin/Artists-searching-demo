import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Card, Bioinfo, Artwork } from '../interface';
import { CARDS } from '../mock-data';
import { CardService } from '../card.service';

@Component({
  selector: 'app-search-form',
  templateUrl: './search-form.component.html',
  styleUrls: ['./search-form.component.css']
})
export class SearchFormComponent implements OnInit {

  value: FormControl = new FormControl('', Validators.required);
  cards: Card[] = [];
  bioinfo?: Bioinfo;
  artworks: Artwork[] = [];
  loading: boolean = false;
  loaded_detail: boolean = true;
  show_result: boolean = false;
  show_detail: boolean = false;
  select_card?: Card;
  selected_tab: string = "bioinfo";


  constructor(
	public cardService: CardService
  ) { }

  ngOnInit(): void {
  }

  clearInput(): void {
	this.value.setValue("");
	this.cards = [];
	this.show_result = false;
	this.show_detail = false;
  }

  onSubmit(): void {
    this.loading = true;
	this.cardService.getCards(this.value.value).subscribe(cards => {
		this.cards = cards;
		console.log(this.cards);
		this.loading = false;
		this.show_result = true;
		this.show_detail = false;
	});
  }

  onSelect(card: Card): void {
	this.show_detail = false;
	this.select_card = card;
	this.loaded_detail = false;
	var loaded_bioinfo: boolean = false;
	var loaded_artworks: boolean = false;
	this.cardService.getArtworks(this.select_card.id).subscribe(artworks => {
		this.artworks = artworks;
		loaded_artworks = true;
		this.loaded_detail = loaded_bioinfo && loaded_artworks;
		this.show_detail = this.loaded_detail;
	})
	this.cardService.getBios(this.select_card.id).subscribe(bios => {
		this.bioinfo = bios;
		loaded_bioinfo = true;
		this.loaded_detail = loaded_bioinfo && loaded_artworks;
		this.show_detail = this.loaded_detail;
	})
	this.selected_tab = "bioinfo";
  }
}
