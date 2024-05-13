import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeAnonymComponent } from './home-anonym.component';

describe('HomeAnonymComponent', () => {
  let component: HomeAnonymComponent;
  let fixture: ComponentFixture<HomeAnonymComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HomeAnonymComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeAnonymComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
