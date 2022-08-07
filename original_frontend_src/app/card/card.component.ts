import { Component, OnInit, Input } from '@angular/core';
import { CardService } from '../card.service';
import { Card } from '../interface';

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.css']
})
export class CardComponent implements OnInit {

  @Input() card_item?: Card;
  @Input() is_selected: boolean = false;

  bgcolor = new String();
  uncheck_color = "#205375";
  check_color = "#112B3C";
  is_hover: boolean = false;

  miss_fig_identifier: string = "/assets/shared/missing_image.png";
  miss_fig_replacer: string = "./assets/artsy_logo.svg";

  constructor(
	public cardService: CardService
  ) { }

  ngOnInit(): void {
	console.log(this.card_item);
	this.bgcolor = this.uncheck_color;
  }

  check(): void {
	this.is_hover = true;
  }

  uncheck(): void {
	this.is_hover = false
  }
}
