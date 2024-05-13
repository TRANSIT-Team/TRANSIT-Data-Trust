import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeNonuserComponent } from './home-nonuser.component';

describe('HomeNonuserComponent', () => {
  let component: HomeNonuserComponent;
  let fixture: ComponentFixture<HomeNonuserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HomeNonuserComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeNonuserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
