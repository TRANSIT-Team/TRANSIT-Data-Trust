import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDataSharingComponent } from './dialog-data-sharing.component';

describe('DialogDataSharingComponent', () => {
  let component: DialogDataSharingComponent;
  let fixture: ComponentFixture<DialogDataSharingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogDataSharingComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DialogDataSharingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
