import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingInfoComponent } from './loading-info.component';

describe('LoadingInfoComponent', () => {
  let component: LoadingInfoComponent;
  let fixture: ComponentFixture<LoadingInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoadingInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoadingInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
