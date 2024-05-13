import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingConfettiComponent } from './loading-confetti.component';

describe('LoadingConfettiComponent', () => {
  let component: LoadingConfettiComponent;
  let fixture: ComponentFixture<LoadingConfettiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoadingConfettiComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoadingConfettiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
