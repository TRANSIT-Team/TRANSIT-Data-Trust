import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutsourceMapComponent } from './outsource-map.component';

describe('OutsourceMapComponent', () => {
  let component: OutsourceMapComponent;
  let fixture: ComponentFixture<OutsourceMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutsourceMapComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OutsourceMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
