import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CmrOverviewComponent } from './cmr-overview.component';

describe('CmrOverviewComponent', () => {
  let component: CmrOverviewComponent;
  let fixture: ComponentFixture<CmrOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CmrOverviewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CmrOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
