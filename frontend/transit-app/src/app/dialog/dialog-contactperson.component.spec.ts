import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogContactpersonComponent } from './dialog-contactperson.component';

describe('DialogContactpersonComponent', () => {
  let component: DialogContactpersonComponent;
  let fixture: ComponentFixture<DialogContactpersonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogContactpersonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogContactpersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
