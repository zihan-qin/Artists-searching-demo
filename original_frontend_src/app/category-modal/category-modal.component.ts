import { Component, OnInit, Input } from '@angular/core';
import { Category, Artwork } from '../interface';
import { CATEGORIES } from '../mock-data';
import { CardService } from '../card.service';

@Component({
  selector: 'app-category-modal',
  templateUrl: './category-modal.component.html',
  styleUrls: ['./category-modal.component.css']
})
export class CategoryModalComponent implements OnInit {

  @Input() artwork?: Artwork;
  @Input() ID?: string;
  ID_id?: string;
  categories?: Category[];
  loading: boolean = false;
  displayStyle: string = 'none';

  constructor(
	private cardService: CardService,
  ) { }

  ngOnInit(): void {
    if (this.ID) {
      this.ID_id = '#' + String(this.ID);
    }
  }

  getData(): void {
    if (this.artwork) {
      this.loading = true;
      this.cardService.getCategories(this.artwork.id).subscribe(categories => {
        this.categories = categories;
        this.loading = false;
        this.displayStyle = 'block';
    })
  }
  }
}
